package com.lakeel.altla.vision.domain.model;

import com.google.firebase.database.Exclude;

public final class UserTexture {

    @Exclude
    public String userId;

    @Exclude
    public String textureId;

    public String name;
}
