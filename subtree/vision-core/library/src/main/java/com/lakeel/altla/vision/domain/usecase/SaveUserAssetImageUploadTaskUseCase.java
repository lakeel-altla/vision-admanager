package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageFileUploadTaskRepository;
import com.lakeel.altla.vision.domain.model.UserAssetImageFileUploadTask;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserAssetImageUploadTaskUseCase {

    @Inject
    UserAssetImageFileUploadTaskRepository userAssetImageFileUploadTaskRepository;

    @Inject
    public SaveUserAssetImageUploadTaskUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull UserAssetImageFileUploadTask userAssetImageFileUploadTask) {
        return Completable.create(e -> {
            userAssetImageFileUploadTaskRepository.save(userAssetImageFileUploadTask);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
