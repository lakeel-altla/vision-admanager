package com.lakeel.altla.vision.domain.model;

import com.google.firebase.database.Exclude;

public final class UserScene {

    @Exclude
    public String userId;

    @Exclude
    public String sceneId;

    public String name;

    public long createdAt;

    public String areaId;
}
