package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.model.AreaDescription;

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
    public Completable execute(@NonNull AreaDescription areaDescription) {
        return Completable.create(e -> {
            userAreaDescriptionRepository.save(areaDescription);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
