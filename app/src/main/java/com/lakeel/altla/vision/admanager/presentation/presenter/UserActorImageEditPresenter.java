package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserActorImageEditView;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserActorImage;
import com.lakeel.altla.vision.domain.usecase.FindDocumentBitmapUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserActorImageUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserActorImageUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserActorImageEditPresenter extends BasePresenter<UserActorImageEditView> {

    private static final String ARG_IMAGE_ID = "imageId";

    private static final String STATE_MODEL = "model";

    @Inject
    FindUserActorImageUseCase findUserActorImageUseCase;

    @Inject
    SaveUserActorImageUseCase saveUserActorImageUseCase;

    @Inject
    FindDocumentBitmapUseCase findDocumentBitmapUseCase;

    @Inject
    CurrentUserResolver currentUserResolver;

    private String imageId;

    private Model model;

    @Inject
    public UserActorImageEditPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@Nullable String imageId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_IMAGE_ID, imageId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments != null) {
            imageId = arguments.getString(ARG_IMAGE_ID);
        }

        if (savedInstanceState == null) {
            model = null;
        } else {
            model = Parcels.unwrap(savedInstanceState.getParcelable(STATE_MODEL));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_MODEL, Parcels.wrap(model));
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        getView().onUpdateHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);
        getView().onUpdateTitle(null);
        getView().onUpdateProgressRingThumbnailVisible(false);

        if (model == null) {
            if (imageId == null) {
                imageId = UUID.randomUUID().toString();
                model = new Model();
                model.userId = currentUserResolver.getUserId();
                model.imageId = imageId;
                getView().onShowNameError(R.string.input_error_name_required);
                getView().onUpdateViewsEnabled(true);
                getView().onUpdateActionSave(canSave());
            } else {
                getView().onUpdateViewsEnabled(false);

                Disposable disposable = findUserActorImageUseCase
                        .execute(imageId)
                        .map(UserActorImageEditPresenter::map)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(model -> {
                            this.model = model;
                            getView().onUpdateTitle(model.name);
                            getView().onUpdateName(model.name);
                            getView().onUpdateViewsEnabled(true);
                            getView().onUpdateActionSave(canSave());
                        }, e -> {
                            getLog().e("Failed.", e);
                            getView().onSnackbar(R.string.snackbar_failed);
                        }, () -> {
                            getLog().e("Entity not found.");
                            getView().onSnackbar(R.string.snackbar_failed);
                        });
                manageDisposable(disposable);
            }
        } else {
            getView().onUpdateTitle(model.name);
            getView().onUpdateName(model.name);
            getView().onUpdateViewsEnabled(true);
            getView().onUpdateActionSave(canSave());

            getView().onUpdateProgressRingThumbnailVisible(true);
            Disposable disposable = findDocumentBitmapUseCase
                    .execute(model.imageUri)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(bitmap -> getView().onUpdateProgressRingThumbnailVisible(false))
                    .doOnError(e -> getView().onUpdateProgressRingThumbnailVisible(false))
                    .subscribe(bitmap -> {
                        getView().onUpdateThumbnail(bitmap);
                    }, e -> {
                        getLog().e("Failed.", e);
                    });
            manageDisposable(disposable);
        }
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        getView().onUpdateHomeAsUpIndicator(null);
    }

    public void onEditTextNameAfterTextChanged(String name) {
        model.name = name;
        getView().onHideNameError();

        // Don't save the empty name.
        if (name == null || name.length() == 0) {
            getView().onShowNameError(R.string.input_error_name_required);
        }

        getView().onUpdateActionSave(canSave());
    }

    public void onImageSelected(@NonNull Uri imageUri) {
        // This method will be called before Fragment#onStart().
        model.imageUri = imageUri;
    }

    private boolean canSave() {
        return model.name != null && model.name.length() != 0;
    }

    public void onClickButtonSelectImage() {
        getView().onShowImagePicker();
    }

    public void onActionSave() {
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);

        UserActorImage userActorImage = map(model);

        Disposable disposable = saveUserActorImageUseCase
                .execute(userActorImage, model.imageUri)
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

    @NonNull
    public static Model map(@NonNull UserActorImage userActorImage) {
        Model model = new Model();
        model.userId = userActorImage.userId;
        model.imageId = userActorImage.imageId;
        model.name = userActorImage.name;
        model.createdAt = userActorImage.createdAt;
        model.updatedAt = userActorImage.updatedAt;
        return model;
    }

    @NonNull
    public static UserActorImage map(@NonNull Model model) {
        UserActorImage userActorImage = new UserActorImage(model.userId, model.imageId);
        userActorImage.name = model.name;
        userActorImage.createdAt = model.createdAt;
        userActorImage.updatedAt = model.updatedAt;
        return userActorImage;
    }

    @Parcel
    public static final class Model {

        String userId;

        String imageId;

        Uri imageUri;

        String name;

        long createdAt = -1;

        long updatedAt = -1;
    }
}
