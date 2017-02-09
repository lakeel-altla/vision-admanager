package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import android.support.annotation.NonNull;

public final class UserAreaDescriptionEditModel {

    public final String userId;

    public final String areaDescriptionId;

    public String name;

    public boolean fileUploaded;

    public String areaId;

    public String areaName;

    public long createdAt;

    public long updatedAt;

    public UserAreaDescriptionEditModel(@NonNull String userId, @NonNull String areaDescriptionId) {
        this.userId = userId;
        this.areaDescriptionId = areaDescriptionId;
    }
}
