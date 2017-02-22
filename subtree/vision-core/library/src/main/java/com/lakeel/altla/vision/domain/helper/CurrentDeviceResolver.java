package com.lakeel.altla.vision.domain.helper;

import android.support.annotation.NonNull;

public final class CurrentDeviceResolver {

    private final String instanceId;

    public CurrentDeviceResolver(@NonNull String instanceId) {
        this.instanceId = instanceId;
    }

    @NonNull
    public String getInstanceId() {
        return instanceId;
    }
}
