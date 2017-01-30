package com.lakeel.altla.vision.domain.model;

import com.google.firebase.database.Exclude;

public final class UserAreaDescriptionScene {

    @Exclude
    public String userId;

    @Exclude
    public String areaDescriptionId;

    @Exclude
    public String sceneId;

    public float translationX;

    public float translationY;

    public float translationZ;

    public float orientationX;

    public float orientationY;

    public float orientationZ;

    public float orientationW;
}
