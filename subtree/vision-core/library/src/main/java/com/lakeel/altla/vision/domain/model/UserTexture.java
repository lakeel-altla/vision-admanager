package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserTexture {

    public final String userId;

    public final String textureId;

    public String name;

    public UserTexture(@NonNull String userId, @NonNull String textureId) {
        this.userId = userId;
        this.textureId = textureId;
    }
}
