package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class TextureFileMetadata {

    public final String userId;

    public final String textureId;

    public long createdAt;

    public long updatedAt;

    public TextureFileMetadata(@NonNull String userId, @NonNull String textureId) {
        this.userId = userId;
        this.textureId = textureId;
    }
}
