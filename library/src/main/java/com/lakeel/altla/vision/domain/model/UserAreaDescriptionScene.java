package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserAreaDescriptionScene {

    public final String userId;

    public final String areaDescriptionId;

    public final String sceneId;

    public float translationX;

    public float translationY;

    public float translationZ;

    public float orientationX;

    public float orientationY;

    public float orientationZ;

    public float orientationW;

    public UserAreaDescriptionScene(@NonNull String userId, @NonNull String areaDescriptionId,
                                    @NonNull String sceneId) {
        this.userId = userId;
        this.areaDescriptionId = areaDescriptionId;
        this.sceneId = sceneId;
    }
}
