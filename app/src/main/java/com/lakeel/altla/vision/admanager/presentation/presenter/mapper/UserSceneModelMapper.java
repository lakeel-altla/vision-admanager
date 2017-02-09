package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserSceneModel;
import com.lakeel.altla.vision.domain.model.UserScene;

import android.support.annotation.NonNull;

public final class UserSceneModelMapper {

    private UserSceneModelMapper() {
    }

    @NonNull
    public static UserSceneModel map(@NonNull UserScene userScene) {
        UserSceneModel model = new UserSceneModel(userScene.userId, userScene.sceneId);
        model.name = userScene.name;
        model.areaId = userScene.areaId;
        model.createdAt = userScene.createdAt;
        model.updatedAt = userScene.updatedAt;
        return model;
    }

    @NonNull
    public static UserScene map(@NonNull UserSceneModel model) {
        UserScene userScene = new UserScene(model.userId, model.sceneId);
        userScene.name = model.name;
        userScene.areaId = model.areaId;
        userScene.createdAt = model.createdAt;
        userScene.updatedAt = model.updatedAt;
        return userScene;
    }
}
