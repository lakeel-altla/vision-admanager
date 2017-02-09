package com.lakeel.altla.vision.domain.mapper;

import com.google.firebase.database.ServerValue;

public final class ServerTimestampMapper {

    private ServerTimestampMapper() {
    }

    public static long map(Object serverTimestamp) {
        return serverTimestamp != null ? (long) serverTimestamp : -1;
    }

    public static Object map(long timestamp) {
        return -1 < timestamp ? timestamp : ServerValue.TIMESTAMP;
    }
}
