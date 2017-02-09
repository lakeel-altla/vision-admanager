package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserScene {

    public final String userId;

    public final String sceneId;

    public String name;

    public String areaId;

    public long createdAt;

    public long updatedAt;

    public UserScene(@NonNull String userId, @NonNull String sceneId) {
        this.userId = userId;
        this.sceneId = sceneId;
    }
}
