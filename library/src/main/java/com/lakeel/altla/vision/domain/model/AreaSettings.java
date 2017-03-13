package com.lakeel.altla.vision.domain.model;

import com.google.firebase.database.Exclude;

import org.parceler.Parcel;
import org.parceler.Transient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Parcel(Parcel.Serialization.BEAN)
public final class AreaSettings extends BaseEntity {

    private int areaScope;

    private String areaId;

    private String areaDescriptionId;

    public int getAreaScope() {
        return areaScope;
    }

    public void setAreaScope(int areaScope) {
        this.areaScope = areaScope;
    }

    @Exclude
    @Transient
    @NonNull
    public AreaScope getAreaScopeAsEnum() {
        return AreaScope.toAreaScope(areaScope);
    }

    public void setAreaScopeAsEnum(@NonNull AreaScope areaScope) {
        this.areaScope = areaScope.getValue();
    }

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
}
