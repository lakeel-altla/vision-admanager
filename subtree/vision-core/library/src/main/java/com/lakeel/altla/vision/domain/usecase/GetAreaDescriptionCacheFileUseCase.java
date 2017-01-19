package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;

import java.io.File;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class GetAreaDescriptionCacheFileUseCase {

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    public GetAreaDescriptionCacheFileUseCase() {
    }

    public Single<File> execute(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return areaDescriptionCacheRepository.getFile(areaDescriptionId)
                                             .subscribeOn(Schedulers.io());
    }
}
