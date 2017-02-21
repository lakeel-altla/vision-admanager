package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserActorImageView;
import com.lakeel.altla.vision.domain.usecase.ObserveUserActorImageUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserActorImagePresenter extends BasePresenter<UserActorImageView> {

    private static final String ARG_IMAGE_ID = "imageId";

    @Inject
    ObserveUserActorImageUseCase observeUserActorImageUseCase;

    private String imageId;

    @Inject
    public UserActorImagePresenter() {
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

        Disposable disposable = observeUserActorImageUseCase
                .execute(imageId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userActorImage -> {
                    getView().onUpdateTitle(userActorImage.name);
                    getView().onUpdateImageId(userActorImage.imageId);
                    getView().onUpdateName(userActorImage.name);
                    getView().onUpdateCreatedAt(userActorImage.createdAt);
                    getView().onUpdateUpdatedAt(userActorImage.updatedAt);
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
