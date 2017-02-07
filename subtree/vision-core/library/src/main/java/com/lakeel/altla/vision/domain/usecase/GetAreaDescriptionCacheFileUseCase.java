package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;

import android.support.annotation.NonNull;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class GetAreaDescriptionCacheFileUseCase {

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    public GetAreaDescriptionCacheFileUseCase() {
    }

    @NonNull
    public Single<File> execute(@NonNull String areaDescriptionId) {
        return Single.<File>create(e -> {
            File file = areaDescriptionCacheRepository.getFile(areaDescriptionId);
            e.onSuccess(file);
        }).subscribeOn(Schedulers.io());
    }
}
