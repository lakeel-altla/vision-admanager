package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UploadUserActorImageFileTaskRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserActorImageRepository;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UploadUserActorImageFileTask;
import com.lakeel.altla.vision.domain.model.UserActorImage;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserActorImageUseCase {

    @Inject
    UserActorImageRepository userActorImageRepository;

    @Inject
    UploadUserActorImageFileTaskRepository uploadUserActorImageFileTaskRepository;

    @Inject
    CurrentDeviceResolver currentDeviceResolver;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public SaveUserActorImageUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull UserActorImage userActorImage, @Nullable Uri imageUri) {
        String userId = currentUserResolver.getUserId();
        String instanceId = currentDeviceResolver.getInstanceId();

        return Completable.create(e -> {
            userActorImageRepository.save(userActorImage);

            // If the image is changed, upload it.
            if (imageUri != null) {
                UploadUserActorImageFileTask task = new UploadUserActorImageFileTask(userId, userActorImage.imageId);
                task.instanceId = instanceId;
                task.sourceUriString = imageUri.toString();
                uploadUserActorImageFileTaskRepository.save(task);
            }

            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
