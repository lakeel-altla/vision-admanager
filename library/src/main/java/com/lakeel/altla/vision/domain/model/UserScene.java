package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserScene extends UserObject {

    public final String sceneId;

    public String name;

    public String areaId;

    public UserScene(@NonNull String userId, @NonNull String sceneId) {
        super(userId);
        this.sceneId = sceneId;
    }
}
