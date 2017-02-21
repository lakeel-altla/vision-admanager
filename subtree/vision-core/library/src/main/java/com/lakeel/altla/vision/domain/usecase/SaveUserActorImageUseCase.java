package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorImageRepository;
import com.lakeel.altla.vision.domain.model.UserActorImage;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserActorImageUseCase {

    @Inject
    UserActorImageRepository userActorImageRepository;

    @Inject
    public SaveUserActorImageUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull UserActorImage userActorImage) {
        return Completable.create(e -> {
            userActorImageRepository.save(userActorImage);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
