package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.data.repository.firebase.ConnectionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserConnectionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.ObservableDataObservable;
import com.lakeel.altla.vision.domain.model.UserConnection;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveConnectionUseCase {

    private static final Log LOG = LogFactory.getLog(ObserveConnectionUseCase.class);

    @Inject
    ConnectionRepository connectionRepository;

    @Inject
    UserConnectionRepository userConnectionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveConnectionUseCase() {
    }

    @NonNull
    public Observable<Boolean> execute() {
        return ObservableDataObservable
                .using(() -> connectionRepository.observe())
                .doOnNext(connected -> LOG.d("The connection state changed: connected = %b", connected))
                .flatMap(this::registerUserConnection)
                .subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> registerUserConnection(Boolean connected) {
        if (connected) {
            String userId = currentUserResolver.getUserId();
            String instanceId = FirebaseInstanceId.getInstance().getId();
            UserConnection userConnection = new UserConnection(userId, instanceId);

            return Observable.create(e -> {
                userConnectionRepository.markAsOnline(userConnection);
                LOG.i("Mark the user online: userId = %s, instanceId = %s", userId, instanceId);
                e.onNext(true);
                e.onComplete();
            });
        } else {
            return Observable.just(false);
        }
    }
}
