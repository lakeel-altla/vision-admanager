package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserAreaDescriptionUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    public SaveUserAreaDescriptionUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull UserAreaDescription userAreaDescription) {
        return Completable.create(e -> {
            userAreaDescriptionRepository.save(userAreaDescription);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
