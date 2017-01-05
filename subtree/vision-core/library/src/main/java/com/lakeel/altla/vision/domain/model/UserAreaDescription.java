package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class UserAreaDescription {

    public final String areaDescriptionId;

    public final String name;

    public final long creationTime;

    public String placeId;

    public int level;

    public boolean fileCached;

    public boolean fileUploaded;

    public UserAreaDescription(String areaDescriptionId, String name, long creationTime) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");
        if (name == null) throw new ArgumentNullException("name");

        this.areaDescriptionId = areaDescriptionId;
        this.name = name;
        this.creationTime = creationTime;
    }
}
