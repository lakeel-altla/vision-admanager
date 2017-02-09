package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import android.support.annotation.NonNull;

public final class UserSceneItemModel {

    public final String userId;

    public final String sceneId;

    public String name;

    public UserSceneItemModel(@NonNull String userId, @NonNull String sceneId) {
        this.userId = userId;
        this.sceneId = sceneId;
    }
}
