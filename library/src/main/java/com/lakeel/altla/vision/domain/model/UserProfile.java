package com.lakeel.altla.vision.domain.model;

import com.google.firebase.database.Exclude;

public final class UserProfile {

    @Exclude
    public String userId;

    public String displayName;

    public String email;

    public String photoUri;
}
