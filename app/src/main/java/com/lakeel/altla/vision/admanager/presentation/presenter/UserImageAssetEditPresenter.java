package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserImageAssetEditView;
import com.lakeel.altla.vision.api.CurrentUser;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.model.ImageAsset;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class UserImageAssetEditPresenter extends BasePresenter<UserImageAssetEditView> {

    private static final String ARG_ASSET_ID = "assetId";

    private static final String STATE_MODEL = "model";

    @Inject
    VisionService visionService;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String assetId;

    private Model model;

    @Inject
    public UserImageAssetEditPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@Nullable String assetId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ASSET_ID, assetId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments != null) {
            assetId = arguments.getString(ARG_ASSET_ID);
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
            if (assetId == null) {
                model = new Model();
                model.asset.setUserId(CurrentUser.getInstance().getUserId());
                getView().onShowNameError(R.string.input_error_name_required);
                getView().onUpdateViewsEnabled(true);
                getView().onUpdateActionSave(model.canSave());
            } else {
                getView().onUpdateViewsEnabled(false);

                Disposable disposable = Maybe
                        .<ImageAsset>create(e -> {
                            visionService.getUserAssetApi().findUserImageAssetById(assetId, asset -> {
                                if (asset == null) {
                                    e.onComplete();
                                } else {
                                    e.onSuccess(asset);
                                }
                            }, e::onError);
                        })
                        .map(Model::new)
                        .subscribe(model -> {
                            this.model = model;
                            getView().onUpdateTitle(model.asset.getName());
                            getView().onUpdateName(model.asset.getName());
                            getView().onUpdateViewsEnabled(true);
                            getView().onUpdateActionSave(model.canSave());

                            Disposable disposable1 = Single.<Uri>create(e -> {
                                visionService.getUserAssetApi()
                                             .getUserImageAssetFileUriById(model.asset.getId(),
                                                                           e::onSuccess, e::onError);
                            }).subscribe(uri -> {
                                getView().onUpdateThumbnail(uri);
                            }, e -> {
                                getLog().e("Failed.", e);
                            });
                            compositeDisposable.add(disposable1);
                        }, e -> {
                            getLog().e("Failed.", e);
                            getView().onSnackbar(R.string.snackbar_failed);
                        }, () -> {
                            getLog().e("Entity not found.");
                            getView().onSnackbar(R.string.snackbar_failed);
                        });
                compositeDisposable.add(disposable);
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

        compositeDisposable.clear();
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

        visionService.getUserAssetApi().saveUserImageAsset(model.asset);
        visionService.getUserAssetApi().registerUserImageAssetFileUploadTask(model.asset.getId(), model.imageUri);
        getView().onSnackbar(R.string.snackbar_done);
        getView().onBackView();
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
