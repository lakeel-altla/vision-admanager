package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.repository.AreaDescriptionCacheRepository;

import java.io.File;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class GetAreaDescriptionCacheUseCase {

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    public GetAreaDescriptionCacheUseCase() {
    }

    public Single<File> execute(String id) {
        if (id == null) throw new ArgumentNullException("id");

        return areaDescriptionCacheRepository.getFile(id)
                                             .subscribeOn(Schedulers.io());
    }
}
