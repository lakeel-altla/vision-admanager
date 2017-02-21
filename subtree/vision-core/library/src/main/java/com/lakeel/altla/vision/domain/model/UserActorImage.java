package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserActorImage {

    public final String userId;

    public final String imageId;

    public String name;

    public long createdAt;

    public long updatedAt;

    public UserActorImage(@NonNull String userId, @NonNull String imageId) {
        this.userId = userId;
        this.imageId = imageId;
    }
}
