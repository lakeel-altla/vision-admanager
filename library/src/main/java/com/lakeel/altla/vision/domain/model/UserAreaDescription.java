package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserAreaDescription extends UserObject {

    public final String areaDescriptionId;

    public String name;

    public boolean fileUploaded;

    public String areaId;

    public UserAreaDescription(@NonNull String userId, @NonNull String areaDescriptionId) {
        super(userId);
        this.areaDescriptionId = areaDescriptionId;
    }
}
