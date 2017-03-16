package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.model.ImportStatus;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionView;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.helper.ObservableData;
import com.lakeel.altla.vision.model.AreaDescription;
import com.lakeel.altla.vision.model.TangoAreaDescription;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionPresenter extends BasePresenter<UserAreaDescriptionView>
        implements TangoWrapper.OnTangoReadyListener {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    VisionService visionService;

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
            TangoAreaDescription tangoAreaDescription = visionService.getTangoAreaDescriptionApi()
                                                                     .findTangoAreaDescriptionById(areaDescriptionId);

            importStatus = (tangoAreaDescription == null) ? ImportStatus.NOT_IMPORTED : ImportStatus.IMPORTED;
            updateActions();
            getView().onUpdateImportStatus(importStatus);
        });
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        visionService.getTangoWrapper().addOnTangoReadyListener(this);

        Disposable disposable = ObservableData
                .using(() -> visionService.getUserAreaDescriptionApi().observeAreaDescriptionById(areaDescriptionId))
                .map(Model::new)
                .flatMap(model -> {
                    // Resolve the area name
                    String areaId = model.areaDescription.getAreaId();
                    if (areaId == null) {
                        return Observable.just(model);
                    } else {
                        return Observable.create(e -> {
                            visionService.getUserAreaApi().findAreaById(areaId, area -> {
                                model.areaName = area.getName();
                                e.onNext(model);
                                e.onComplete();
                            }, e::onError);
                        });
                    }
                })
                .flatMap(model -> {
                    // Check if the cache file exists.
                    File file = visionService.getUserAreaDescriptionApi()
                                             .getAreaDescriptionCacheById(areaDescriptionId);

                    model.fileCached = file.exists();
                    return Observable.just(model);
                })
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

        visionService.getTangoWrapper().removeOnTangoReadyListener(this);
    }

    public void onActionImport() {
        File file = visionService.getUserAreaDescriptionApi()
                                 .getAreaDescriptionCacheById(areaDescriptionId);
        getView().onShowImportActivity(file);
    }

    public void onActionEdit() {
        getView().onShowUserAreaDescriptionEditView(areaDescriptionId);
    }

    public void onActionUpload() {
        prevBytesTransferred = 0;

        // TODO: use the background service.
        Disposable disposable = Completable
                .create(e -> {
                    visionService.getUserAreaDescriptionApi().uploadAreaDescription(
                            areaDescriptionId, aVoid -> e.onComplete(), e::onError, (totalBytes, bytesTransferred) -> {
                                long increment = bytesTransferred - prevBytesTransferred;
                                prevBytesTransferred = bytesTransferred;
                                getView().onProgressUpdated(totalBytes, increment);
                            });

                })
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

        // TODO: use the background service.
        Disposable disposable = Completable
                .create(e -> {
                    visionService.getUserAreaDescriptionApi().downloadAreaDescription(
                            areaDescriptionId, aVoid -> e.onComplete(), e::onError, (totalBytes, bytesTransferred) -> {
                                long increment = bytesTransferred - prevBytesTransferred;
                                prevBytesTransferred = bytesTransferred;
                                getView().onProgressUpdated(totalBytes, increment);
                            });
                })
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
        visionService.getUserAreaDescriptionApi().deleteAreaDescriptionCacheById(areaDescriptionId);
        fileCached = false;
        updateActions();
        getView().onUpdateFileCached(fileCached);
        getView().onSnackbar(R.string.snackbar_done);
    }

    public void onActionDelete() {
        getView().onShowDeleteConfirmationDialog();
    }

    public void onImported() {
        getView().onSnackbar(R.string.snackbar_done);
    }

    public void onDelete() {
        visionService.getUserAreaDescriptionApi().deleteAreaDescriptionById(areaDescriptionId);
        getView().onSnackbar(R.string.snackbar_done);
        getView().onBackView();
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
