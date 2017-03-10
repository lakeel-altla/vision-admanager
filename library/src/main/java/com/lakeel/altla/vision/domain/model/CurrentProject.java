package com.lakeel.altla.vision.domain.model;

import org.parceler.Parcel;

import android.support.annotation.Nullable;

@Parcel(Parcel.Serialization.BEAN)
public final class CurrentProject extends BaseEntity {

    private String areaId;

    private String areaDescriptionId;

    private String sceneId;

    @Nullable
    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(@Nullable String areaId) {
        this.areaId = areaId;
    }

    @Nullable
    public String getAreaDescriptionId() {
        return areaDescriptionId;
    }

    public void setAreaDescriptionId(@Nullable String areaDescriptionId) {
        this.areaDescriptionId = areaDescriptionId;
    }

    @Nullable
    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(@Nullable String sceneId) {
        this.sceneId = sceneId;
    }
}
