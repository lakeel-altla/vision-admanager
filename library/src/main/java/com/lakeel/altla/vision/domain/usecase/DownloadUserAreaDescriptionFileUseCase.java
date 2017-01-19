package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionFileRepository;

import javax.inject.Inject;

import rx.Completable;

public final class DownloadUserAreaDescriptionFileUseCase {

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    public DownloadUserAreaDescriptionFileUseCase() {
    }

    public Completable execute(String areaDescriptionId, OnProgressListener onProgressListener) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return areaDescriptionCacheRepository
                .getFile(areaDescriptionId)
                .flatMapCompletable(file -> userAreaDescriptionFileRepository.download(
                        user.getUid(), areaDescriptionId, file, onProgressListener));
    }
}
