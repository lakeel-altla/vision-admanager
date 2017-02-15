package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.ImportStatus;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionView;
import com.lakeel.altla.vision.domain.model.UserArea;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.usecase.DeleteAreaDescriptionCacheUseCase;
import com.lakeel.altla.vision.domain.usecase.DeleteUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.DownloadUserAreaDescriptionFileUseCase;
import com.lakeel.altla.vision.domain.usecase.FindTangoAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.GetAreaDescriptionCacheFileUseCase;
import com.lakeel.altla.vision.domain.usecase.UploadUserAreaDescriptionFileUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionPresenter extends BasePresenter<UserAreaDescriptionView>
        implements TangoWrapper.OnTangoReadyListener {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

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

        Disposable disposable = findUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .map(userAreaDescription -> {
                    Model model = new Model();
                    model.userAreaDescription = userAreaDescription;
                    return model;
                })
                .flatMap(model -> {
                    // Resolve the area name
                    if (model.userAreaDescription.areaId != null) {
                        return findUserAreaUseCase
                                .execute(model.userAreaDescription.areaId)
                                .map(userArea -> {
                                    model.userArea = userArea;
                                    return model;
                                });
                    } else {
                        return Maybe.just(model);
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
                            .toMaybe();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    updateActions();
                    getView().onUpdateTitle(model.userAreaDescription.name);
                    getView().onUpdateAreaDescriptionId(areaDescriptionId);
                    getView().onUpdateImportStatus(importStatus);
                    getView().onUpdateFileUploaded(model.userAreaDescription.fileUploaded);
                    getView().onUpdateFileCached(model.fileCached);
                    getView().onUpdateName(model.userAreaDescription.name);
                    getView().onUpdateAreaName(model.userArea.name);
                    getView().onUpdateCreatedAt(model.userAreaDescription.createdAt);
                    getView().onUpdateUpdatedAt(model.userAreaDescription.updatedAt);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                }, () -> {
                    getLog().e("Entity not found.");
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
                    getView().onDeleted();
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

        UserAreaDescription userAreaDescription;

        UserArea userArea;

        boolean fileCached;
    }
}
