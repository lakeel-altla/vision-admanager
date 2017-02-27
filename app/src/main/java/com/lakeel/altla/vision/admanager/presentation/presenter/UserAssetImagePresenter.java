package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAssetImageView;
import com.lakeel.altla.vision.domain.usecase.GetUserAssetImageFileUriUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveUserAssetImageUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAssetImagePresenter extends BasePresenter<UserAssetImageView> {

    private static final String ARG_IMAGE_ID = "imageId";

    @Inject
    ObserveUserAssetImageUseCase observeUserAssetImageUseCase;

    @Inject
    GetUserAssetImageFileUriUseCase getUserAssetImageFileUriUseCase;

    private String imageId;

    @Inject
    public UserAssetImagePresenter() {
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

        Disposable disposable = observeUserAssetImageUseCase
                .execute(imageId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userActorImage -> {
                    getView().onUpdateTitle(userActorImage.name);
                    getView().onUpdateImageId(userActorImage.assetId);
                    getView().onUpdateName(userActorImage.name);
                    getView().onUpdateCreatedAt(userActorImage.createdAt);
                    getView().onUpdateUpdatedAt(userActorImage.updatedAt);

                    Disposable disposable1 = getUserAssetImageFileUriUseCase
                            .execute(userActorImage.assetId)
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
