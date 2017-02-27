package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageFileUploadTaskRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageRepository;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserAssetImageFileUploadTask;
import com.lakeel.altla.vision.domain.model.UserAssetImage;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserAssetImageUseCase {

    @Inject
    UserAssetImageRepository userAssetImageRepository;

    @Inject
    UserAssetImageFileUploadTaskRepository userAssetImageFileUploadTaskRepository;

    @Inject
    CurrentDeviceResolver currentDeviceResolver;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public SaveUserAssetImageUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull UserAssetImage userAssetImage, @Nullable Uri imageUri) {
        String userId = currentUserResolver.getUserId();
        String instanceId = currentDeviceResolver.getInstanceId();

        return Completable.create(e -> {
            userAssetImageRepository.save(userAssetImage);

            // If the image is changed, upload it.
            if (imageUri != null) {
                UserAssetImageFileUploadTask task = new UserAssetImageFileUploadTask(userId, userAssetImage.assetId);
                task.instanceId = instanceId;
                task.sourceUriString = imageUri.toString();
                userAssetImageFileUploadTaskRepository.save(task);
            }

            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
