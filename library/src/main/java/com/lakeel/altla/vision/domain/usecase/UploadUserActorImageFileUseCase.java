package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.data.repository.android.DocumentRepository;
import com.lakeel.altla.vision.data.repository.firebase.UploadUserActorImageFileTaskRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserActorImageFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserActorImageRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Progress;

import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class UploadUserActorImageFileUseCase {

    private static final Log LOG = LogFactory.getLog(UploadUserActorImageFileUseCase.class);

    @Inject
    UserActorImageRepository userActorImageRepository;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    UserActorImageFileRepository userActorImageFileRepository;

    @Inject
    UploadUserActorImageFileTaskRepository uploadUserActorImageFileTaskRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public UploadUserActorImageFileUseCase() {
    }

    @NonNull
    public Observable<Progress> execute(@NonNull String imageId, @NonNull String sourceUriString) {
        String userId = currentUserResolver.getUserId();

        return Observable.<Progress>create(e -> {
            userActorImageRepository.find(userId, imageId, userActorImage -> {
                if (userActorImage == null) {
                    throw new IllegalStateException(String.format("Entity not found: imageId = %s", imageId));
                }

                try {
                    InputStream inputStream =
                            new BufferedInputStream(documentRepository.openInputStream(sourceUriString));
                    long totalBytes = inputStream.available();

                    userActorImageFileRepository.upload(userId, imageId, inputStream, aVoid -> {
                        // Uploaded.

                        // Update the status.
                        userActorImage.fileUploaded = true;
                        userActorImageRepository.save(userActorImage);

                        // Delete the task.
                        uploadUserActorImageFileTaskRepository.delete(userId, imageId);

                        close(inputStream);
                        e.onComplete();
                    }, ex -> {
                        // Failed.
                        close(inputStream);
                        e.onError(ex);
                    }, (_totalBytes, bytesTransferred) -> {
                        // Progress.
                        Progress progress = new Progress(totalBytes, bytesTransferred);
                        e.onNext(progress);
                    });
                } catch (IOException ex) {
                    e.onError(ex);
                }
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }

    private void close(@NonNull InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException ex) {
            LOG.e("Failed to close the stream.", ex);
        }
    }
}
