package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.helper.RxHelper;
import com.lakeel.altla.vision.admanager.presentation.view.UserImageAssetView;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class UserImageAssetPresenter extends BasePresenter<UserImageAssetView> {

    private static final String ARG_ASSET_ID = "assetId";

    @Inject
    VisionService visionService;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String assetId;

    @Inject
    public UserImageAssetPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull String assetId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ASSET_ID, assetId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String assetId = arguments.getString(ARG_ASSET_ID);
        if (assetId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_ASSET_ID));
        }

        this.assetId = assetId;
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        Disposable disposable = RxHelper
                .usingData(() -> visionService.getUserAssetApi().observeUserImageAssetById(assetId))
                .subscribe(asset -> {
                    getView().onUpdateTitle(asset.getName());
                    getView().onUpdateImageId(asset.getId());
                    getView().onUpdateName(asset.getName());
                    getView().onUpdateCreatedAt(asset.getCreatedAtAsLong());
                    getView().onUpdateUpdatedAt(asset.getUpdatedAtAsLong());

                    Disposable disposable1 = Single.<Uri>create(e -> {
                        visionService.getUserAssetApi()
                                     .getUserImageAssetFileUriById(asset.getId(), e::onSuccess, e::onError);
                    }).subscribe(uri -> {
                        getView().onUpdateThumbnail(uri);
                    }, e -> {
                        getLog().e("Failed.", e);
                    });
                    compositeDisposable.add(disposable1);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        compositeDisposable.clear();
    }

    public void onEdit() {
        getView().onShowUserActorImageEditView(assetId);
    }
}
