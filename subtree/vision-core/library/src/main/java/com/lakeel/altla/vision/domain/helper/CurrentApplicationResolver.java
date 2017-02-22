package com.lakeel.altla.vision.domain.helper;

import android.support.annotation.NonNull;

public final class CurrentApplicationResolver {

    private final String applicationId;

    public CurrentApplicationResolver(@NonNull String applicationId) {
        this.applicationId = applicationId;
    }

    @NonNull
    public String getApplicationId() {
        return applicationId;
    }
}
