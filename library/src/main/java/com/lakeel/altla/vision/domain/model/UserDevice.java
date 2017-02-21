package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserDevice {

    public final String userId;

    public final String instanceId;

    public String osName;

    public String osModel;

    public String osVersion;

    public long createdAt;

    public long updatedAt;

    public UserDevice(@NonNull String userId, @NonNull String instanceId) {
        this.userId = userId;
        this.instanceId = instanceId;
    }
}
