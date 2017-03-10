package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.data.repository.firebase.UserDeviceConnectionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.DeviceConnection;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SignOutUseCase {

    private static final Log LOG = LogFactory.getLog(SignOutUseCase.class);

    @Inject
    UserDeviceConnectionRepository userDeviceConnectionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public SignOutUseCase() {
    }

    @NonNull
    public Completable execute() {
        String userId = currentUserResolver.getUserId();
        String instanceId = FirebaseInstanceId.getInstance().getId();

        return Completable.create(e -> {
            userDeviceConnectionRepository.find(userId, instanceId, connection -> {
                if (connection == null) {
                    LOG.v("The user device connection does not exist: userId = %s, instanceId = %s", userId,
                          instanceId);
                    connection = new DeviceConnection();
                    connection.setId(instanceId);
                    connection.setUserId(userId);
                }
                connection.setOnline(false);
                connection.setLastOnlineAtAsLong(-1);

                userDeviceConnectionRepository.save(connection);

                FirebaseAuth.getInstance().signOut();

                e.onComplete();
            }, ex -> {
                FirebaseAuth.getInstance().signOut();

                e.onError(ex);
            });
        }).subscribeOn(Schedulers.io());
    }
}
