package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.data.repository.firebase.UserConnectionRepository;
import com.lakeel.altla.vision.domain.model.UserConnection;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SignOutUseCase {

    @Inject
    UserConnectionRepository userConnectionRepository;

    @Inject
    public SignOutUseCase() {
    }

    @NonNull
    public Completable execute() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) throw new IllegalStateException("The current user not found.");

        return Completable.create(e -> {

            String userId = firebaseUser.getUid();
            String instanceId = FirebaseInstanceId.getInstance().getId();
            UserConnection userConnection = new UserConnection(userId, instanceId);

            userConnectionRepository.markAsOffline(userConnection);
            FirebaseAuth.getInstance().signOut();
            e.onComplete();

        }).subscribeOn(Schedulers.io());
    }
}
