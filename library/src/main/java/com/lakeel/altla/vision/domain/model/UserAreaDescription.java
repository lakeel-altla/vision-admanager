package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserAreaDescription {

    public final String userId;

    public final String areaDescriptionId;

    public String name;

    public boolean fileUploaded;

    public String areaId;

    public long createdAt = -1;

    public long updatedAt = -1;

    public UserAreaDescription(@NonNull String userId, @NonNull String areaDescriptionId) {
        this.userId = userId;
        this.areaDescriptionId = areaDescriptionId;
    }
}
