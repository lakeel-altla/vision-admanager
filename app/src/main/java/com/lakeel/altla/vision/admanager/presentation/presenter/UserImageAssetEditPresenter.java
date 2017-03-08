package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserImageAssetEditView;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.ImageAsset;
import com.lakeel.altla.vision.domain.usecase.FindDocumentBitmapUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserImageAssetUseCase;
import com.lakeel.altla.vision.domain.usecase.GetUserImageAssetFileUriUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserImageAssetUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserImageAssetEditPresenter extends BasePresenter<UserImageAssetEditView> {

    private static final String ARG_IMAGE_ID = "imageId";

    private static final String STATE_MODEL = "model";

    @Inject
    FindUserImageAssetUseCase findUserImageAssetUseCase;

    @Inject
    SaveUserImageAssetUseCase saveUserImageAssetUseCase;

    @Inject
    FindDocumentBitmapUseCase findDocumentBitmapUseCase;

    @Inject
    GetUserImageAssetFileUriUseCase getUserImageAssetFileUriUseCase;

    @Inject
    CurrentUserResolver currentUserResolver;

    private String imageId;

    private Model model;

    @Inject
    public UserImageAssetEditPresenter() {
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
                model = new Model();
                model.asset.setUserId(currentUserResolver.getUserId());
                getView().onShowNameError(R.string.input_error_name_required);
                getView().onUpdateViewsEnabled(true);
                getView().onUpdateActionSave(model.canSave());
            } else {
                getView().onUpdateViewsEnabled(false);

                Disposable disposable = findUserImageAssetUseCase
                        .execute(imageId)
                        .map(Model::new)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(model -> {
                            this.model = model;
                            getView().onUpdateTitle(model.asset.getName());
                            getView().onUpdateName(model.asset.getName());
                            getView().onUpdateViewsEnabled(true);
                            getView().onUpdateActionSave(model.canSave());

                            Disposable disposable1 = getUserImageAssetFileUriUseCase
                                    .execute(model.asset.getId())
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
            getView().onUpdateTitle(model.asset.getName());
            getView().onUpdateName(model.asset.getName());
            getView().onUpdateThumbnail(model.imageUri);
            getView().onUpdateViewsEnabled(true);
            getView().onUpdateActionSave(model.canSave());
        }
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        getView().onUpdateHomeAsUpIndicator(null);
    }

    public void onEditTextNameAfterTextChanged(String value) {
        model.asset.setName(value);
        getView().onHideNameError();

        // Don't save the empty value.
        if (value == null || value.length() == 0) {
            getView().onShowNameError(R.string.input_error_name_required);
        }

        getView().onUpdateActionSave(model.canSave());
    }

    public void onImageSelected(@NonNull Uri imageUri) {
        // This method will be called before Fragment#onStart().
        model.imageUri = imageUri;
        model.asset.setFileUploaded(false);
    }

    public void onClickButtonSelectImage() {
        getView().onShowImagePicker();
    }

    public void onActionSave() {
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);

        Disposable disposable = saveUserImageAssetUseCase
                .execute(model.asset, model.imageUri)
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

    @Parcel
    public static final class Model {

        ImageAsset asset;

        Uri imageUri;

        Model() {
            this(new ImageAsset());
        }

        Model(@NonNull ImageAsset asset) {
            this.asset = asset;
        }

        boolean canSave() {
            return asset.getName() != null && asset.getName().length() != 0;
        }
    }
}
