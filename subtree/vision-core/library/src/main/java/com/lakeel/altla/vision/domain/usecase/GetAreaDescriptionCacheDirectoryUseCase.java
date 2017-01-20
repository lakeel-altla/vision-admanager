package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class GetAreaDescriptionCacheDirectoryUseCase {

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    public GetAreaDescriptionCacheDirectoryUseCase() {
    }

    public Single<File> execute() {
        return Single.<File>create(e -> {
            File file = areaDescriptionCacheRepository.getDirectory();
            e.onSuccess(file);
        }).subscribeOn(Schedulers.io());
    }
}
