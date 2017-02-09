package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;

import android.support.annotation.NonNull;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DownloadUserAreaDescriptionFileUseCase {

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public DownloadUserAreaDescriptionFileUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull String areaDescriptionId, OnProgressListener onProgressListener) {
        String userId = currentUserResolver.getUserId();

        return Completable.create(e -> {
            File file = areaDescriptionCacheRepository.getFile(areaDescriptionId);
            userAreaDescriptionFileRepository.download(
                    userId, areaDescriptionId, file,
                    aVoid -> e.onComplete(),
                    e::onError,
                    onProgressListener);
        }).subscribeOn(Schedulers.io());
    }
}
