package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;

import javax.inject.Inject;

import rx.Completable;
import rx.schedulers.Schedulers;

public final class DeleteAreaDescriptionCacheUseCase {

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    public DeleteAreaDescriptionCacheUseCase() {
    }

    public Completable execute(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return areaDescriptionCacheRepository
                .delete(areaDescriptionId)
                .subscribeOn(Schedulers.io());
    }
}
