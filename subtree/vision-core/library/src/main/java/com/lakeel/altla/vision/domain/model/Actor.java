package com.lakeel.altla.vision.domain.model;

import com.google.firebase.database.Exclude;

import org.parceler.Parcel;
import org.parceler.Transient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Parcel(Parcel.Serialization.BEAN)
public final class Actor extends BaseEntity {

    private String sceneId;

    @Transient
    private AssetType assetType;

    private String assetId;

    private String name;

    private double positionX;

    private double positionY;

    private double positionZ;

    private double orientationX;

    private double orientationY;

    private double orientationZ;

    private double orientationW;

    private double scaleX = 1;

    private double scaleY = 1;

    private double scaleZ = 1;

    @Nullable
    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(@Nullable String sceneId) {
        this.sceneId = sceneId;
    }

    @Exclude
    @Nullable
    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(@Nullable AssetType assetType) {
        this.assetType = assetType;
    }

    public int getAssetTypeAsInt() {
        return assetType == null ? AssetType.UNKNOWN.getValue() : assetType.getValue();
    }

    @Nullable
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(@Nullable String assetId) {
        this.assetId = assetId;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public double getPositionZ() {
        return positionZ;
    }

    public void setPositionZ(double positionZ) {
        this.positionZ = positionZ;
    }

    public double getOrientationX() {
        return orientationX;
    }

    public void setOrientationX(double orientationX) {
        this.orientationX = orientationX;
    }

    public double getOrientationY() {
        return orientationY;
    }

    public void setOrientationY(double orientationY) {
        this.orientationY = orientationY;
    }

    public double getOrientationZ() {
        return orientationZ;
    }

    public void setOrientationZ(double orientationZ) {
        this.orientationZ = orientationZ;
    }

    public double getOrientationW() {
        return orientationW;
    }

    public void setOrientationW(double orientationW) {
        this.orientationW = orientationW;
    }

    public double getScaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    public double getScaleZ() {
        return scaleZ;
    }

    public void setScaleZ(double scaleZ) {
        this.scaleZ = scaleZ;
    }

    public enum AssetType {
        UNKNOWN(0),
        IMAGE(1);

        private final int value;

        AssetType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @NonNull
        public static AssetType toAssetType(int value) {
            for (AssetType assetType : AssetType.values()) {
                if (assetType.value == value) {
                    return assetType;
                }
            }

            throw new IllegalArgumentException("Unknown value: " + value);
        }
    }
}
