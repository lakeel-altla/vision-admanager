package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import android.support.annotation.NonNull;

public final class EditUserAreaDescriptionModel {

    public final String areaDescriptionId;

    public long creationTime;

    public String name;

    public EditUserAreaDescriptionModel(@NonNull String areaDescriptionId) {
        this.areaDescriptionId = areaDescriptionId;
    }
}
