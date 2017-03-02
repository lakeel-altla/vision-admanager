package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserCurrentProject extends UserObject {

    public final String instanceId;

    public String areaId;

    public String areaDescriptionId;

    public String sceneId;

    public UserCurrentProject(@NonNull String userId, @NonNull String instanceId) {
        super(userId);
        this.instanceId = instanceId;
    }
}
