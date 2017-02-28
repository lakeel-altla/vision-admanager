package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserArea extends UserObject {

    public final String areaId;

    public String name;

    public String placeId;

    public int level;

    public UserArea(@NonNull String userId, @NonNull String areaId) {
        super(userId);
        this.areaId = areaId;
    }
}
