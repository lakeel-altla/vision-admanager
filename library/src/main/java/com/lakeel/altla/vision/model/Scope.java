package com.lakeel.altla.vision.model;

import android.support.annotation.NonNull;

public enum Scope {
    UNKNOWN(0),
    PUBLIC(1),
    USER(2);

    private final int value;

    Scope(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @NonNull
    public static Scope toAreaScope(int value) {
        for (Scope scope : Scope.values()) {
            if (scope.getValue() == value) {
                return scope;
            }
        }
        return Scope.UNKNOWN;
    }
}
