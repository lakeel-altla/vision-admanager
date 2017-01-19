package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DatabaseReference;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserDevice;

import rx.Completable;
import rx.CompletableSubscriber;

public final class UserDeviceRepository {

    private static final Log LOG = LogFactory.getLog(UserDeviceRepository.class);

    private static final String PATH_USER_DEVICES = "userDevices";

    private final DatabaseReference rootReference;

    public UserDeviceRepository(DatabaseReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    public Completable save(UserDevice userDevice) {
        if (userDevice == null) throw new ArgumentNullException("userDevice");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                rootReference.child(PATH_USER_DEVICES)
                             .child(userDevice.userId)
                             .child(userDevice.instanceId)
                             .setValue(userDevice, (error, reference) -> {
                                 if (error != null) {
                                     LOG.e(String.format("Failed to save: reference = %s", reference),
                                           error.toException());
                                 }
                             });

                subscriber.onCompleted();
            }
        });
    }
}
