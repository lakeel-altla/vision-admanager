package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.model.Area;

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
    public Completable execute(@NonNull Area area) {
        return Completable.create(e -> {
            userAreaRepository.save(area);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
