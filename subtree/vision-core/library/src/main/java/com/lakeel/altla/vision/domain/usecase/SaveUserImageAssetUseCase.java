package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetFileUploadTaskRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetRepository;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.ImageAsset;
import com.lakeel.altla.vision.domain.model.ImageAssetFileUploadTask;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserImageAssetUseCase {

    @Inject
    UserImageAssetRepository userImageAssetRepository;

    @Inject
    UserImageAssetFileUploadTaskRepository userImageAssetFileUploadTaskRepository;

    @Inject
    CurrentDeviceResolver currentDeviceResolver;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public SaveUserImageAssetUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull ImageAsset asset, @Nullable Uri imageUri) {
        String userId = currentUserResolver.getUserId();
        String instanceId = currentDeviceResolver.getInstanceId();

        return Completable.create(e -> {
            userImageAssetRepository.save(asset);

            // If the image is changed, upload it.
            if (imageUri != null) {
                ImageAssetFileUploadTask task = new ImageAssetFileUploadTask();
                task.setId(asset.getId());
                task.setUserId(userId);
                task.setInstanceId(instanceId);
                task.setSourceUriString(imageUri.toString());
                userImageAssetFileUploadTaskRepository.save(task);
            }

            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
