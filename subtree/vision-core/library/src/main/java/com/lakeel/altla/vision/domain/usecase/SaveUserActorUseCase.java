package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorRepository;
import com.lakeel.altla.vision.domain.model.Actor;

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
    public Completable execute(@NonNull Actor actor) {
        return Completable.create(e -> {
            userActorRepository.save(actor);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
