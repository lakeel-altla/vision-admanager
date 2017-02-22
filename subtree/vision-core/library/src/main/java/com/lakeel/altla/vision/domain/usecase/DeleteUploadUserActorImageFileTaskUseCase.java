package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UploadUserActorImageFileTaskRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DeleteUploadUserActorImageFileTaskUseCase {

    @Inject
    UploadUserActorImageFileTaskRepository uploadUserActorImageFileTaskRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public DeleteUploadUserActorImageFileTaskUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull String imageId) {
        String userId = currentUserResolver.getUserId();

        return Completable.create(e -> {
            uploadUserActorImageFileTaskRepository.delete(userId, imageId);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
