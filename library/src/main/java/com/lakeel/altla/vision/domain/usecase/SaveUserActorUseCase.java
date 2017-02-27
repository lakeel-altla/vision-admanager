package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorRepository;
import com.lakeel.altla.vision.domain.model.UserActor;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserActorUseCase {

    @Inject
    UserActorRepository userActorRepository;

    @Inject
    public SaveUserActorUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull UserActor userActor) {
        return Completable.create(e -> {
            userActorRepository.save(userActor);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
