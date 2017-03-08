package com.lakeel.altla.vision.domain.model;

import org.parceler.Parcel;

import android.support.annotation.Nullable;

@Parcel(Parcel.Serialization.BEAN)
public final class Scene extends BaseEntity {

    private String name;

    private String areaId;

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(@Nullable String areaId) {
        this.areaId = areaId;
    }
}
