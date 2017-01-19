package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserDevice;

public final class UserDeviceRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserDeviceRepository.class);

    private static final String PATH_USER_DEVICES = "userDevices";

    public UserDeviceRepository(FirebaseDatabase database) {
        super(database);
    }

    public void save(UserDevice userDevice) {
        if (userDevice == null) throw new ArgumentNullException("userDevice");

        getDatabase().getReference()
                     .child(PATH_USER_DEVICES)
                     .child(userDevice.userId)
                     .child(userDevice.instanceId)
                     .setValue(userDevice, (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }
}
