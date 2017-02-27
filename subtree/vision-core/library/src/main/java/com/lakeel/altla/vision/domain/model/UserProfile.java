package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserProfile extends UserObject {

    public String displayName;

    public String email;

    public String photoUri;

    public UserProfile(@NonNull String userId) {
        super(userId);
    }
}
