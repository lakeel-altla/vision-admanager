package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserSceneModel;
import com.lakeel.altla.vision.domain.model.UserScene;

import android.support.annotation.NonNull;

public final class UserSceneModelMapper {

    private UserSceneModelMapper() {
    }

    @NonNull
    public static UserSceneModel map(@NonNull UserScene userScene) {
        UserSceneModel model = new UserSceneModel();
        model.areaId = userScene.areaId;
        model.name = userScene.name;
        model.createdAt = userScene.createdAt;
        return model;
    }
}
