package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserAreaUseCase {

    @Inject
    UserAreaRepository userAreaRepository;

    @Inject
    public SaveUserAreaUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull UserArea userArea) {
        return Completable.create(e -> {
            userAreaRepository.save(userArea);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
