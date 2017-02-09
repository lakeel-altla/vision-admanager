package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import android.support.annotation.NonNull;

public final class UserSceneModel {

    public final String userId;

    public final String sceneId;

    public String name;

    public String areaId;

    public long createdAt = -1;

    public long updatedAt = -1;

    public UserSceneModel(@NonNull String userId, @NonNull String sceneId) {
        this.userId = userId;
        this.sceneId = sceneId;
    }
}
