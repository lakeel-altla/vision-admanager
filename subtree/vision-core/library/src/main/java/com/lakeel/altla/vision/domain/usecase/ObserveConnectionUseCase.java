package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.data.repository.firebase.ConnectionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserDeviceConnectionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.model.DeviceConnection;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveConnectionUseCase {

    private static final Log LOG = LogFactory.getLog(ObserveConnectionUseCase.class);

    @Inject
    ConnectionRepository connectionRepository;

    @Inject
    UserDeviceConnectionRepository userDeviceConnectionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveConnectionUseCase() {
    }

    @NonNull
    public Observable<Boolean> execute() {
        return ObservableData
                .using(() -> connectionRepository.observe())
                .doOnNext(connected -> LOG.i("The user device connection state changed: connected = %b", connected))
                .flatMap(this::registerUserDeviceConnection)
                .subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> registerUserDeviceConnection(Boolean connected) {
        if (connected) {
            String userId = currentUserResolver.getUserId();
            String instanceId = FirebaseInstanceId.getInstance().getId();

            return Observable.create(e -> {
                userDeviceConnectionRepository.find(userId, instanceId, connection -> {
                    if (connection == null) {
                        connection = new DeviceConnection();
                        connection.setId(instanceId);
                        connection.setUserId(userId);
                    }
                    connection.setOnline(true);

                    userDeviceConnectionRepository.save(connection);
                    userDeviceConnectionRepository.markAsOfflineWhenDisconnected(userId, instanceId);

                    e.onNext(true);
                    e.onComplete();
                }, e::onError);
            });
        } else {
            return Observable.just(false);
        }
    }
}
