package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public class UserAsset extends UserObject {

    public final String assetId;

    public String name;

    protected UserAsset(@NonNull String userId, @NonNull String assetId) {
        super(userId);
        this.assetId = assetId;
    }
}
