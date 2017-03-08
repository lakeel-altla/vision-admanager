package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetFileUploadTaskRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DeleteUserImageAssetFileUploadTaskUseCase {

    @Inject
    UserImageAssetFileUploadTaskRepository userImageAssetFileUploadTaskRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public DeleteUserImageAssetFileUploadTaskUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull String assetId) {
        String userId = currentUserResolver.getUserId();

        return Completable.create(e -> {
            userImageAssetFileUploadTaskRepository.delete(userId, assetId);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
