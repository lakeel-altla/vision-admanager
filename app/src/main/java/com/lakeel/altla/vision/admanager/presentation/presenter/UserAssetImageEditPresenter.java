package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAssetImageEditView;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserAssetImage;
import com.lakeel.altla.vision.domain.usecase.FindDocumentBitmapUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAssetImageUseCase;
import com.lakeel.altla.vision.domain.usecase.GetUserAssetImageFileUriUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserAssetImageUseCase;
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

public final class UserAssetImageEditPresenter extends BasePresenter<UserAssetImageEditView> {

    private static final String ARG_IMAGE_ID = "imageId";

    private static final String STATE_MODEL = "model";

    @Inject
    FindUserAssetImageUseCase findUserAssetImageUseCase;

    @Inject
    SaveUserAssetImageUseCase saveUserAssetImageUseCase;

    @Inject
    FindDocumentBitmapUseCase findDocumentBitmapUseCase;

    @Inject
    GetUserAssetImageFileUriUseCase getUserAssetImageFileUriUseCase;

    @Inject
    CurrentUserResolver currentUserResolver;

    private String imageId;

    private Model model;

    @Inject
    public UserAssetImageEditPresenter() {
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

                Disposable disposable = findUserAssetImageUseCase
                        .execute(imageId)
                        .map(UserAssetImageEditPresenter::map)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(model -> {
                            this.model = model;
                            getView().onUpdateTitle(model.name);
                            getView().onUpdateName(model.name);
                            getView().onUpdateViewsEnabled(true);
                            getView().onUpdateActionSave(canSave());

                            Disposable disposable1 = getUserAssetImageFileUriUseCase
                                    .execute(model.imageId)
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
                        }, () -> {
                            getLog().e("Entity not found.");
                            getView().onSnackbar(R.string.snackbar_failed);
                        });
                manageDisposable(disposable);
            }
        } else {
            getView().onUpdateTitle(model.name);
            getView().onUpdateName(model.name);
            getView().onUpdateThumbnail(model.imageUri);
            getView().onUpdateViewsEnabled(true);
            getView().onUpdateActionSave(canSave());
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
        model.fileUploaded = false;
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

        UserAssetImage userAssetImage = map(model);

        Disposable disposable = saveUserAssetImageUseCase
                .execute(userAssetImage, model.imageUri)
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
    public static Model map(@NonNull UserAssetImage userAssetImage) {
        Model model = new Model();
        model.userId = userAssetImage.userId;
        model.imageId = userAssetImage.assetId;
        model.fileUploaded = userAssetImage.fileUploaded;
        model.name = userAssetImage.name;
        model.createdAt = userAssetImage.createdAt;
        model.updatedAt = userAssetImage.updatedAt;
        return model;
    }

    @NonNull
    public static UserAssetImage map(@NonNull Model model) {
        UserAssetImage userAssetImage = new UserAssetImage(model.userId, model.imageId);
        userAssetImage.fileUploaded = model.fileUploaded;
        userAssetImage.name = model.name;
        userAssetImage.createdAt = model.createdAt;
        userAssetImage.updatedAt = model.updatedAt;
        return userAssetImage;
    }

    @Parcel
    public static final class Model {

        String userId;

        String imageId;

        boolean fileUploaded;

        Uri imageUri;

        String name;

        long createdAt = -1;

        long updatedAt = -1;
    }
}
