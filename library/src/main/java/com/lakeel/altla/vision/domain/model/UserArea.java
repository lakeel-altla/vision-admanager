package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserArea {

    public final String userId;

    public final String areaId;

    public String name;

    public String placeId;

    public int level;

    public long createdAt;

    public long updatedAt;

    public UserArea(@NonNull String userId, @NonNull String areaId) {
        this.userId = userId;
        this.areaId = areaId;
    }
}
