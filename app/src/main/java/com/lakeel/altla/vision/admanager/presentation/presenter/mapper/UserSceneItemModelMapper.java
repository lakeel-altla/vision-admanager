package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserSceneItemModel;
import com.lakeel.altla.vision.domain.model.UserScene;

import android.support.annotation.NonNull;

public final class UserSceneItemModelMapper {

    private UserSceneItemModelMapper() {
    }

    @NonNull
    public static UserSceneItemModel map(@NonNull UserScene userScene) {
        UserSceneItemModel model = new UserSceneItemModel(userScene.userId, userScene.sceneId);
        model.name = userScene.name;
        return model;
    }
}
