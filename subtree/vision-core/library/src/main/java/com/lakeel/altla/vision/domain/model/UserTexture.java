package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class UserTexture {

    public String userId;

    public final String textureId;

    public final String name;

    public UserTexture(String userId, String textureId, String name) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (textureId == null) throw new ArgumentNullException("textureId");
        if (name == null) throw new ArgumentNullException("name");

        this.userId = userId;
        this.textureId = textureId;
        this.name = name;
    }
}
