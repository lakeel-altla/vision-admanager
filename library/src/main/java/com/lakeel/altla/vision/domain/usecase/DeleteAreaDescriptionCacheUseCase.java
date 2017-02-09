package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DeleteAreaDescriptionCacheUseCase {

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    public DeleteAreaDescriptionCacheUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull String areaDescriptionId) {
        return Completable.create(e -> {
            areaDescriptionCacheRepository.delete(areaDescriptionId);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
