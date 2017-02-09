package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import android.support.annotation.NonNull;

public final class UserAreaDescriptionModel {

    public final String userId;

    public final String areaDescriptionId;

    public String name;

    public boolean fileUploaded;

    public String areaId;

    public String areaName;

    public ImportStatus importStatus = ImportStatus.UNKNOWN;

    public boolean fileCached;

    public long createdAt;

    public long updatedAt;

    public UserAreaDescriptionModel(@NonNull String userId, @NonNull String areaDescriptionId) {
        this.userId = userId;
        this.areaDescriptionId = areaDescriptionId;
    }
}
