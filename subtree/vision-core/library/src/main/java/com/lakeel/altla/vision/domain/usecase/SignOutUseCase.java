package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.data.repository.firebase.UserConnectionRepository;
import com.lakeel.altla.vision.domain.model.UserConnection;

import javax.inject.Inject;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.schedulers.Schedulers;

public final class SignOutUseCase {

    @Inject
    UserConnectionRepository userConnectionRepository;

    @Inject
    public SignOutUseCase() {
    }

    public Completable execute() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            throw new IllegalStateException("The current user not found.");
        }

        String userId = firebaseUser.getUid();
        String instanceId = FirebaseInstanceId.getInstance().getId();
        UserConnection userConnection = new UserConnection(userId, instanceId);

        return Completable
                .create(new Completable.OnSubscribe() {
                    @Override
                    public void call(CompletableSubscriber subscriber) {
                        userConnectionRepository.markAsOffline(userConnection);
                        FirebaseAuth.getInstance().signOut();
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io());
    }
}
