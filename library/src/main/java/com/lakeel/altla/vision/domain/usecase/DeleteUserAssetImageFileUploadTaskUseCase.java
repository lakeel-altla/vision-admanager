package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageFileUploadTaskRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DeleteUserAssetImageFileUploadTaskUseCase {

    @Inject
    UserAssetImageFileUploadTaskRepository userAssetImageFileUploadTaskRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public DeleteUserAssetImageFileUploadTaskUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull String assetId) {
        String userId = currentUserResolver.getUserId();

        return Completable.create(e -> {
            userAssetImageFileUploadTaskRepository.delete(userId, assetId);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
