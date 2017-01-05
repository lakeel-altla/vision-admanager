package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import android.support.annotation.NonNull;

public final class EditUserAreaDescriptionModel {

    public final String areaDescriptionId;

    public long creationTime;

    public String name;

    public String placeId;

    public String placeName;

    public String placeAddress;

    public int level;

    public EditUserAreaDescriptionModel(@NonNull String areaDescriptionId) {
        this.areaDescriptionId = areaDescriptionId;
    }
}
