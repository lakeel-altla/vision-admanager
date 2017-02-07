package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DeleteUserAreaDescriptionUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    public DeleteUserAreaDescriptionUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull String areaDescriptionId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user in not signed in.");

        String userId = user.getUid();

        return Completable.create(e -> userAreaDescriptionFileRepository.delete(userId, areaDescriptionId, aVoid -> {
            userAreaDescriptionRepository.delete(userId, areaDescriptionId);
            e.onComplete();
        }, e::onError)).subscribeOn(Schedulers.io());
    }
}
