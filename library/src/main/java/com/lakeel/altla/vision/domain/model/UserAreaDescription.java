package com.lakeel.altla.vision.domain.model;

import com.google.firebase.database.Exclude;

public final class UserAreaDescription {

    @Exclude
    public String userId;

    @Exclude
    public String areaDescriptionId;

    public String name;

    public long createdAt;

    public boolean fileUploaded;

    public String areaId;
}
