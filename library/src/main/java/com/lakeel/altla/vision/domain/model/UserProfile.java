package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserProfile {

    public final String userId;

    public String displayName;

    public String email;

    public String photoUri;

    public long createdAt;

    public long updatedAt;

    public UserProfile(@NonNull String userId) {
        this.userId = userId;
    }
}
