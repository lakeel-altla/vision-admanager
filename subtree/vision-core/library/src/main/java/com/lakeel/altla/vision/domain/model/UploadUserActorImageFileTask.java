package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UploadUserActorImageFileTask {

    public final String userId;

    public final String imageId;

    public String instanceId;

    public String sourceUriString;

    public long createdAt = -1;

    public UploadUserActorImageFileTask(@NonNull String userId, @NonNull String imageId) {
        this.userId = userId;
        this.imageId = imageId;
    }
}
