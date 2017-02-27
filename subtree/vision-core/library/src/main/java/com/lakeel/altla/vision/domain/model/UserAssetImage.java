package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserAssetImage extends UserAsset {

    public String name;

    public boolean fileUploaded;

    public UserAssetImage(@NonNull String userId, @NonNull String assetId) {
        super(userId, assetId);
    }
}
