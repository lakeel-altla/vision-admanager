package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserDevice extends UserObject {

    public final String instanceId;

    public String osName;

    public String osModel;

    public String osVersion;

    public UserDevice(@NonNull String userId, @NonNull String instanceId) {
        super(userId);
        this.instanceId = instanceId;
    }
}
