package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class UserAreaDescription {

    public final String userId;

    public final String areaDescriptionId;

    public final String name;

    public final long creationTime;

    public boolean synced;

    public UserAreaDescription(String userId, String areaDescriptionId, String name, long creationTime) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");
        if (name == null) throw new ArgumentNullException("name");

        this.userId = userId;
        this.areaDescriptionId = areaDescriptionId;
        this.name = name;
        this.creationTime = creationTime;
    }
}
