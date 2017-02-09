package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class TangoAreaDescription {

    public final String areaDescriptionId;

    public String name;

    public long createdAt;

    public TangoAreaDescription(@NonNull String areaDescriptionId) {
        this.areaDescriptionId = areaDescriptionId;
    }
}
