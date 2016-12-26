package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class UserProfile {

    public final String userId;

    public String displayName;

    public String email;

    public String photoUri;

    public UserProfile(String userId) {
        if (userId == null) throw new ArgumentNullException("userId");

        this.userId = userId;
    }
}
