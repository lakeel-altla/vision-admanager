package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionView;
import com.lakeel.altla.vision.domain.usecase.DeleteAreaDescriptionCacheUseCase;
import com.lakeel.altla.vision.domain.usecase.DeleteUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.DownloadUserAreaDescriptionFileUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.GetAreaDescriptionCacheFileUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.domain.usecase.UploadUserAreaDescriptionFileUseCase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionPresenter extends BasePresenter<UserAreaDescriptionView> {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

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

    private String areaDescriptionId;

    private UserAreaDescriptionModel model;

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
    protected void onStartOverride() {
        super.onStartOverride();

        Disposable disposable = findUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .map(userAreaDescription -> {
                    UserAreaDescriptionModel model = new UserAreaDescriptionModel();
                    model.areaDescriptionId = areaDescriptionId;
                    model.name = userAreaDescription.name;
                    model.createdAt = userAreaDescription.createdAt;
                    model.fileUploaded = userAreaDescription.fileUploaded;
                    model.areaId = userAreaDescription.areaId;
                    return model;
                })
                .flatMap(model -> {
                    // Resolve the area name
                    if (model.areaId != null) {
                        return findUserAreaUseCase
                                .execute(model.areaId)
                                .map(userArea -> {
                                    model.areaName = userArea.name;
                                    return model;
                                });
                    } else {
                        return Maybe.just(model);
                    }
                })
                .flatMapSingle(model -> {
                    // Check if the cache file exists.
                    return getAreaDescriptionCacheFileUseCase
                            .execute(model.areaDescriptionId)
                            .map(file -> {
                                model.fileCached = file.exists();
                                return model;
                            });
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    this.model = model;
                    updateMenus();
                    getView().onModelUpdated(model);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                });
        manageDisposable(disposable);
    }

    public void onActionImport() {
        Disposable disposable = getAreaDescriptionCacheFileUseCase
                .execute(areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getView()::onShowImportActivity, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
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
                    model.fileUploaded = true;
                    updateMenus();
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
                    model.fileCached = true;
                    updateMenus();
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onActionDeleteCache() {
        Disposable disposable = deleteAreaDescriptionCacheUseCase
                .execute(areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    model.fileCached = false;
                    updateMenus();
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
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
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    private void updateMenus() {
        getView().onUpdateUploadMenu(!model.fileUploaded && model.fileCached);
        getView().onUpdateDownloadMenu(model.fileUploaded && !model.fileCached);
        getView().onUpdateDeleteCacheMenu(model.fileUploaded && model.fileCached);
    }
}
