package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import android.support.annotation.NonNull;

public final class UserAreaModel {

    public final String userId;

    public final String areaId;

    public String name;

    public String placeId;

    public PlaceModel place;

    public int level;

    public long createdAt = -1;

    public long updatedAt = -1;

    public UserAreaModel(@NonNull String userId, @NonNull String areaId) {
        this.userId = userId;
        this.areaId = areaId;
    }
}
