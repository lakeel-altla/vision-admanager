package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public class UserObject {

    public final String userId;

    public long createdAt = -1;

    public long updatedAt = -1;

    protected UserObject(@NonNull String userId) {
        this.userId = userId;
    }
}
