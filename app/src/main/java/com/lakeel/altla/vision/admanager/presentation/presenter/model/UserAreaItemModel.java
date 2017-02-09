package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import android.support.annotation.NonNull;

public final class UserAreaItemModel {

    public final String userId;

    public final String areaId;

    public String name;

    public String placeId;

    public PlaceModel place;

    public String level;

    public UserAreaItemModel(@NonNull String userId, @NonNull String areaId) {
        this.userId = userId;
        this.areaId = areaId;
    }
}
