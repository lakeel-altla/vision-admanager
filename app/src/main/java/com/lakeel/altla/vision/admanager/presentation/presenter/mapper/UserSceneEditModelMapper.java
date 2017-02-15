package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserSceneEditModel;
import com.lakeel.altla.vision.domain.model.UserScene;

import android.support.annotation.NonNull;

public final class UserSceneEditModelMapper {

    private UserSceneEditModelMapper() {
    }

    @NonNull
    public static UserSceneEditModel map(@NonNull UserScene userScene) {
        UserSceneEditModel model = new UserSceneEditModel();
        model.userId = userScene.userId;
        model.sceneId = userScene.sceneId;
        model.name = userScene.name;
        model.areaId = userScene.areaId;
        model.createdAt = userScene.createdAt;
        model.updatedAt = userScene.updatedAt;
        return model;
    }

    @NonNull
    public static UserScene map(@NonNull UserSceneEditModel model) {
        UserScene userScene = new UserScene(model.userId, model.sceneId);
        userScene.name = model.name;
        userScene.areaId = model.areaId;
        userScene.createdAt = model.createdAt;
        userScene.updatedAt = model.updatedAt;
        return userScene;
    }
}
