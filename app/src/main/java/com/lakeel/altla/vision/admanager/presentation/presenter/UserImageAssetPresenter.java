package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserImageAssetView;
import com.lakeel.altla.vision.domain.usecase.GetUserImageAssetFileUriUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveUserImageAssetUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserImageAssetPresenter extends BasePresenter<UserImageAssetView> {

    private static final String ARG_IMAGE_ID = "imageId";

    @Inject
    ObserveUserImageAssetUseCase observeUserImageAssetUseCase;

    @Inject
    GetUserImageAssetFileUriUseCase getUserImageAssetFileUriUseCase;

    private String imageId;

    @Inject
    public UserImageAssetPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull String imageId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_IMAGE_ID, imageId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String imageId = arguments.getString(ARG_IMAGE_ID);
        if (imageId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_IMAGE_ID));
        }

        this.imageId = imageId;
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        Disposable disposable = observeUserImageAssetUseCase
                .execute(imageId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(asset -> {
                    getView().onUpdateTitle(asset.getName());
                    getView().onUpdateImageId(asset.getId());
                    getView().onUpdateName(asset.getName());
                    getView().onUpdateCreatedAt(asset.getCreatedAtAsLong());
                    getView().onUpdateUpdatedAt(asset.getUpdatedAtAsLong());

                    Disposable disposable1 = getUserImageAssetFileUriUseCase
                            .execute(asset.getId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(uri -> {
                                getView().onUpdateThumbnail(uri);
                            }, e -> {
                                getLog().e("Failed.", e);
                            });
                    manageDisposable(disposable1);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onEdit() {
        getView().onShowUserActorImageEditView(imageId);
    }
}
