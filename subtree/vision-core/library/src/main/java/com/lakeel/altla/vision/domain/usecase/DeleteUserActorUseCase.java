package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DeleteUserActorUseCase {

    @Inject
    UserActorRepository userActorRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public DeleteUserActorUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull String areaId, @NonNull String actorId) {
        String userId = currentUserResolver.getUserId();

        return Completable.create(e -> {
            userActorRepository.delete(userId, areaId, actorId);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
