package com.lakeel.altla.vision.domain.model;

import com.google.firebase.database.Exclude;

public final class UserDevice {

    @Exclude
    public String userId;

    @Exclude
    public String instanceId;

    public long creationTime;

    public String osName;

    public String osModel;

    public String osVersion;
}
