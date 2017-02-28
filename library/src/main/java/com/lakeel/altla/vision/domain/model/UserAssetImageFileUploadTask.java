package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserAssetImageFileUploadTask extends UserObject {

    public final String assetId;

    public String instanceId;

    public String sourceUriString;

    public UserAssetImageFileUploadTask(@NonNull String userId, @NonNull String assetId) {
        super(userId);
        this.assetId = assetId;
    }
}
