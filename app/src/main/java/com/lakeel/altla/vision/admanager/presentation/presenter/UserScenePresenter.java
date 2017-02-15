package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserSceneEditModelMapper;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneView;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserSceneUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserScenePresenter extends BasePresenter<UserSceneView> {

    private static final String ARG_SCENE_ID = "sceneId";

    @Inject
    FindUserSceneUseCase findUserSceneUseCase;

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
    protected void onStartOverride() {
        super.onStartOverride();

        Disposable disposable = findUserSceneUseCase
                .execute(sceneId)
                .map(UserSceneEditModelMapper::map)
                .flatMap(model -> {
                    if (model.areaId == null) {
                        return Maybe.just(model);
                    } else {
                        return findUserAreaUseCase
                                .execute(model.areaId)
                                .map(userArea -> {
                                    model.areaName = userArea.name;
                                    return model;
                                });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    getView().onUpdateTitle(model.name);
                    getView().onUpdateSceneId(model.sceneId);
                    getView().onUpdateName(model.name);
                    getView().onUpdateAreaName(model.areaName);
                    getView().onUpdateCreatedAt(model.createdAt);
                    getView().onUpdateUpdatedAt(model.updatedAt);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                }, () -> {
                    getLog().e("Entity not found.");
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onEdit() {
        getView().onShowUserSceneEditView(sceneId);
    }
}
