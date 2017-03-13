package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.ImportStatus;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionView;
import com.lakeel.altla.vision.domain.model.AreaDescription;
import com.lakeel.altla.vision.domain.model.AreaScope;
import com.lakeel.altla.vision.domain.usecase.DeleteAreaDescriptionCacheUseCase;
import com.lakeel.altla.vision.domain.usecase.DeleteUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.DownloadUserAreaDescriptionFileUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.FindTangoAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.GetAreaDescriptionCacheFileUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.UploadUserAreaDescriptionFileUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionPresenter extends BasePresenter<UserAreaDescriptionView>
        implements TangoWrapper.OnTangoReadyListener {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    ObserveUserAreaDescriptionUseCase observeUserAreaDescriptionUseCase;

    @Inject
    FindAreaUseCase findAreaUseCase;

    @Inject
    FindTangoAreaDescriptionUseCase findTangoAreaDescriptionUseCase;

    @Inject
    GetAreaDescriptionCacheFileUseCase getAreaDescriptionCacheFileUseCase;

    @Inject
    UploadUserAreaDescriptionFileUseCase uploadUserAreaDescriptionFileUseCase;

    @Inject
    DownloadUserAreaDescriptionFileUseCase downloadUserAreaDescriptionFileUseCase;

    @Inject
    DeleteAreaDescriptionCacheUseCase deleteAreaDescriptionCacheUseCase;

    @Inject
    DeleteUserAreaDescriptionUseCase deleteUserAreaDescriptionUseCase;

    @Inject
    TangoWrapper tangoWrapper;

    private String areaDescriptionId;

    private ImportStatus importStatus = ImportStatus.UNKNOWN;

    private boolean fileUploaded;

    private boolean fileCached;

    private long prevBytesTransferred;

    @Inject
    public UserAreaDescriptionPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull String areaDescriptionId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_DESCRIPTION_ID, areaDescriptionId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new IllegalStateException("Arguments must be not null.");

        String areaDescriptionId = arguments.getString(ARG_AREA_DESCRIPTION_ID);
        if (areaDescriptionId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_AREA_DESCRIPTION_ID));
        }

        this.areaDescriptionId = areaDescriptionId;
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    public void onTangoReady(Tango tango) {
        runOnUiThread(() -> {
            Disposable disposable = findTangoAreaDescriptionUseCase
                    .execute(tango, areaDescriptionId)
                    .map(tangoAreaDescription -> ImportStatus.IMPORTED)
                    .defaultIfEmpty(ImportStatus.NOT_IMPORTED)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(importStatus -> {
                        this.importStatus = importStatus;
                        updateActions();
                        getView().onUpdateImportStatus(importStatus);
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            manageDisposable(disposable);
        });
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        tangoWrapper.addOnTangoReadyListener(this);

        Disposable disposable = observeUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .map(Model::new)
                .flatMap(model -> {
                    // Resolve the area name
                    if (model.areaDescription.getAreaId() == null) {
                        return Observable.just(model);
                    } else {
                        return findAreaUseCase
                                .execute(AreaScope.USER, model.areaDescription.getAreaId())
                                .map(userArea -> {
                                    model.areaName = userArea.getName();
                                    return model;
                                })
                                .toObservable();
                    }
                })
                .flatMap(model -> {
                    // Check if the cache file exists.
                    return getAreaDescriptionCacheFileUseCase
                            .execute(areaDescriptionId)
                            .map(file -> {
                                model.fileCached = file.exists();
                                return model;
                            })
                            .toObservable();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    fileUploaded = model.areaDescription.isFileUploaded();
                    fileCached = model.fileCached;
                    updateActions();
                    getView().onUpdateTitle(model.areaDescription.getName());
                    getView().onUpdateAreaDescriptionId(model.areaDescription.getId());
                    getView().onUpdateImportStatus(importStatus);
                    getView().onUpdateFileUploaded(model.areaDescription.isFileUploaded());
                    getView().onUpdateFileCached(model.fileCached);
                    getView().onUpdateName(model.areaDescription.getName());
                    getView().onUpdateAreaName(model.areaName);
                    getView().onUpdateCreatedAt(model.areaDescription.getCreatedAtAsLong());
                    getView().onUpdateUpdatedAt(model.areaDescription.getUpdatedAtAsLong());
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        tangoWrapper.removeOnTangoReadyListener(this);
    }

    public void onActionImport() {
        Disposable disposable = getAreaDescriptionCacheFileUseCase
                .execute(areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((file) -> {
                    getView().onShowImportActivity(file);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onActionEdit() {
        getView().onShowUserAreaDescriptionEditView(areaDescriptionId);
    }

    public void onActionUpload() {
        prevBytesTransferred = 0;

        Disposable disposable = uploadUserAreaDescriptionFileUseCase
                .execute(areaDescriptionId, (totalBytes, bytesTransferred) -> {
                    long increment = bytesTransferred - prevBytesTransferred;
                    prevBytesTransferred = bytesTransferred;
                    getView().onProgressUpdated(totalBytes, increment);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_subscription -> getView().onShowProgressDialog(R.string.progress_dialog_upload))
                .doOnTerminate(() -> getView().onHideProgressDialog())
                .subscribe(() -> {
                    fileUploaded = true;
                    updateActions();
                    getView().onUpdateFileUploaded(fileUploaded);
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onActionDownload() {
        prevBytesTransferred = 0;

        Disposable disposable = downloadUserAreaDescriptionFileUseCase
                .execute(areaDescriptionId, (totalBytes, bytesTransferred) -> {
                    long increment = bytesTransferred - prevBytesTransferred;
                    prevBytesTransferred = bytesTransferred;
                    getView().onProgressUpdated(totalBytes, increment);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_subscription -> getView().onShowProgressDialog(R.string.progress_dialog_download))
                .doOnTerminate(() -> getView().onHideProgressDialog())
                .subscribe(() -> {
                    fileCached = true;
                    updateActions();
                    getView().onUpdateFileCached(fileCached);
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onActionDeleteCache() {
        Disposable disposable = deleteAreaDescriptionCacheUseCase
                .execute(areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    fileCached = false;
                    updateActions();
                    getView().onUpdateFileCached(fileCached);
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onActionDelete() {
        getView().onShowDeleteConfirmationDialog();
    }

    public void onImported() {
        getView().onSnackbar(R.string.snackbar_done);
    }

    public void onDelete() {
        Disposable disposable = deleteUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getView().onSnackbar(R.string.snackbar_done);
                    getView().onBackView();
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    private void updateActions() {
        getView().onUpdateActionImport(canImport());
        getView().onUpdateActionUpload(canUpload());
        getView().onUpdateActionDownload(canDownload());
        getView().onUpdateActionDeleteCache(canDeleteCache());
    }

    private boolean canImport() {
        return importStatus == ImportStatus.NOT_IMPORTED;
    }

    private boolean canUpload() {
        return !fileUploaded && fileCached;
    }

    private boolean canDownload() {
        return fileUploaded && !fileCached;
    }

    private boolean canDeleteCache() {
        return fileUploaded && fileCached;
    }

    private final class Model {

        final AreaDescription areaDescription;

        boolean fileCached;

        String areaName;

        Model(@NonNull AreaDescription areaDescription) {
            this.areaDescription = areaDescription;
        }
    }
}
