package com.lakeel.altla.vision.model;

import android.support.annotation.NonNull;

public enum AreaScope {
    UNKNOWN(0),
    PUBLIC(1),
    USER(2);

    private final int value;

    AreaScope(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @NonNull
    public static AreaScope toAreaScope(int value) {
        for (AreaScope areaScope : AreaScope.values()) {
            if (areaScope.getValue() == value) {
                return areaScope;
            }
        }
        return AreaScope.UNKNOWN;
    }
}
