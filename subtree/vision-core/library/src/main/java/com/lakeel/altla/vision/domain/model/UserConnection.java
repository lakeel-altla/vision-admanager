package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserConnection {

    public final String userId;

    public final String instanceId;

    public UserConnection(@NonNull String userId, @NonNull String instanceId) {
        this.userId = userId;
        this.instanceId = instanceId;
    }
}
