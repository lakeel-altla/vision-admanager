package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneView;
import com.lakeel.altla.vision.domain.model.Scene;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveUserSceneUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserScenePresenter extends BasePresenter<UserSceneView> {

    private static final String ARG_SCENE_ID = "sceneId";

    @Inject
    ObserveUserSceneUseCase observeUserSceneUseCase;

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    private String sceneId;

    @Inject
    public UserScenePresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull String areaId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SCENE_ID, areaId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String sceneId = arguments.getString(ARG_SCENE_ID, null);
        if (sceneId == null) {
            throw new IllegalArgumentException(String.format("Argument '%s' must be not null.", ARG_SCENE_ID));
        }

        this.sceneId = sceneId;
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        Disposable disposable = observeUserSceneUseCase
                .execute(sceneId)
                .map(Model::new)
                .flatMap(model -> {
                    if (model.scene.getAreaId() == null) {
                        return Observable.just(model);
                    } else {
                        return findUserAreaUseCase
                                .execute(model.scene.getAreaId())
                                .map(userArea -> {
                                    model.areaName = userArea.getName();
                                    return model;
                                })
                                .toObservable();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    getView().onUpdateTitle(model.scene.getName());
                    getView().onUpdateSceneId(model.scene.getId());
                    getView().onUpdateName(model.scene.getName());
                    getView().onUpdateAreaName(model.areaName);
                    getView().onUpdateCreatedAt(model.scene.getCreatedAtAsLong());
                    getView().onUpdateUpdatedAt(model.scene.getUpdatedAtAsLong());
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onEdit() {
        getView().onShowUserSceneEditView(sceneId);
    }

    private final class Model {

        final Scene scene;

        String areaName;

        Model(@NonNull Scene scene) {
            this.scene = scene;
        }
    }
}
