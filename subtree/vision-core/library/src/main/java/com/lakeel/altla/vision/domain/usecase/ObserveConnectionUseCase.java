package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.model.UserConnection;
import com.lakeel.altla.vision.domain.repository.ConnectionRepository;
import com.lakeel.altla.vision.domain.repository.UserConnectionRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class ObserveConnectionUseCase {

    private static final Log LOG = LogFactory.getLog(ObserveConnectionUseCase.class);

    @Inject
    ConnectionRepository connectionRepository;

    @Inject
    UserConnectionRepository userConnectionRepository;

    @Inject
    public ObserveConnectionUseCase() {
    }

    public Observable<Boolean> execute() {
        return connectionRepository
                .observe()
                .doOnNext(connected -> LOG.d("The connection state changed: connected = %b", connected))
                .flatMap(this::registerUserConnection)
                .subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> registerUserConnection(Boolean connected) {
        if (connected) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) throw new IllegalStateException("The user is not signed in.");

            String userId = user.getUid();
            String instanceId = FirebaseInstanceId.getInstance().getId();
            UserConnection userConnection = new UserConnection(userId, instanceId);

            return userConnectionRepository
                    .markAsOnline(userConnection)
                    .doOnSuccess(_userConnection -> LOG.i("Mark the user online: userId = %s, instanceId = %s",
                                                          userId, instanceId))
                    .toObservable()
                    .map(_userConnection -> true);
        } else {
            return Observable.just(false);
        }
    }
}
