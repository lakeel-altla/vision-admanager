package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetFileUploadTaskRepository;
import com.lakeel.altla.vision.domain.model.ImageAssetFileUploadTask;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserAssetImageUploadTaskUseCase {

    @Inject
    UserImageAssetFileUploadTaskRepository userImageAssetFileUploadTaskRepository;

    @Inject
    public SaveUserAssetImageUploadTaskUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull ImageAssetFileUploadTask imageAssetFileUploadTask) {
        return Completable.create(e -> {
            userImageAssetFileUploadTaskRepository.save(imageAssetFileUploadTask);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
