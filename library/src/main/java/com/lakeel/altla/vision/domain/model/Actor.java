package com.lakeel.altla.vision.domain.model;

import org.parceler.Parcel;

import android.support.annotation.Nullable;

@Parcel(Parcel.Serialization.BEAN)
public final class Actor extends BaseEntity {

    public static final int ASSET_TYPE_UNKNOWN = 0;

    public static final int ASSET_TYPE_IMAGE = 1;

    public static final int LAYER_COMMERCIAL = 0;

    public static final int LAYER_NONCOMMERCIAL = 1;

    private String areaId;

    private int assetType;

    private String assetId;

    private int layer;

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
    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(@Nullable String areaId) {
        this.areaId = areaId;
    }

    public int getAssetType() {
        return assetType;
    }

    public void setAssetType(int assetType) {
        this.assetType = assetType;
    }

    @Nullable
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(@Nullable String assetId) {
        this.assetId = assetId;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
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
}
