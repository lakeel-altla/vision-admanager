package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserActor extends UserObject {

    public final String sceneId;

    public final String actorId;

    public AssetType assetType;

    public String assetId;

    public String name;

    public double positionX;

    public double positionY;

    public double positionZ;

    public double orientationX;

    public double orientationY;

    public double orientationZ;

    public double orientationW;

    public double scaleX = 1;

    public double scaleY = 1;

    public double scaleZ = 1;

    public long createdAt = -1;

    public long updatedAt = -1;

    public UserActor(@NonNull String userId, @NonNull String sceneId, @NonNull String actorId) {
        super(userId);
        this.sceneId = sceneId;
        this.actorId = actorId;
    }

    public enum AssetType {
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
