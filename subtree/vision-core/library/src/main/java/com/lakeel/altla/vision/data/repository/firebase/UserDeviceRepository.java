package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.mapper.ServerTimestampMapper;
import com.lakeel.altla.vision.domain.model.UserDevice;

import android.support.annotation.NonNull;

public final class UserDeviceRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserDeviceRepository.class);

    private static final String PATH_USER_DEVICES = "userDevices";

    public UserDeviceRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserDevice userDevice) {
        getDatabase().getReference()
                     .child(PATH_USER_DEVICES)
                     .child(userDevice.userId)
                     .child(userDevice.instanceId)
                     .setValue(map(userDevice), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    @NonNull
    private static Value map(@NonNull UserDevice userDevice) {
        Value value = new Value();
        value.osName = userDevice.osName;
        value.osModel = userDevice.osModel;
        value.osVersion = userDevice.osVersion;
        value.createdAt = ServerTimestampMapper.map(userDevice.createdAt);
        value.updatedAt = ServerValue.TIMESTAMP;
        return value;
    }

    public static final class Value {

        public String osName;

        public String osModel;

        public String osVersion;

        public Object createdAt;

        public Object updatedAt;
    }
}
