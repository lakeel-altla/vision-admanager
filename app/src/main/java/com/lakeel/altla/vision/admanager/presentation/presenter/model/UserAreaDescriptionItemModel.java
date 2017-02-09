package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import android.support.annotation.NonNull;

public final class UserAreaDescriptionItemModel {

    public final String userId;

    public final String areaDescriptionId;

    public String name;

    public UserAreaDescriptionItemModel(@NonNull String userId, @NonNull String areaDescriptionId) {
        this.userId = userId;
        this.areaDescriptionId = areaDescriptionId;
    }
}
