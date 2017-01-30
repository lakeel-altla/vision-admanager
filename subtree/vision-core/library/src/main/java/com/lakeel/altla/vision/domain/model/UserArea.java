package com.lakeel.altla.vision.domain.model;

import com.google.firebase.database.Exclude;

public final class UserArea {

    @Exclude
    public String userId;

    @Exclude
    public String areaId;

    public String name;

    public long createdAt;

    public String placeId;

    public int level;
}
