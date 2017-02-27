package com.lakeel.altla.vision.domain.model;

import android.support.annotation.NonNull;

public final class UserActor {

    public final String userId;

    public final String sceneId;

    public final String actorId;

    public AssetType assetType;

    public String assetId;

    public float positionX;

    public float positionY;

    public float positionZ;

    public float orientationX;

    public float orientationY;

    public float orientationZ;

    public float orientationW;

    public long createdAt = -1;

    public long updatedAt = -1;

    public UserActor(@NonNull String userId, @NonNull String sceneId, @NonNull String actorId) {
        this.userId = userId;
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
        public static AssetType toModelType(int value) {
            for (AssetType assetType : AssetType.values()) {
                if (assetType.value == value) {
                    return assetType;
                }
            }

            throw new IllegalArgumentException("Unknown value: " + value);
        }
    }
}
