package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.data.repository.android.DocumentRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageFileUploadTaskRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Progress;

import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class UploadUserAssetImageFileUseCase {

    private static final Log LOG = LogFactory.getLog(UploadUserAssetImageFileUseCase.class);

    @Inject
    UserAssetImageRepository userAssetImageRepository;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    UserAssetImageFileRepository userAssetImageFileRepository;

    @Inject
    UserAssetImageFileUploadTaskRepository userAssetImageFileUploadTaskRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public UploadUserAssetImageFileUseCase() {
    }

    @NonNull
    public Observable<Progress> execute(@NonNull String assetId, @NonNull String sourceUriString) {
        String userId = currentUserResolver.getUserId();

        return Observable.<Progress>create(e -> {
            userAssetImageRepository.find(userId, assetId, userActorImage -> {
                if (userActorImage == null) {
                    throw new IllegalStateException(String.format("Entity not found: assetId = %s", assetId));
                }

                try {
                    InputStream inputStream =
                            new BufferedInputStream(documentRepository.openInputStream(sourceUriString));
                    long totalBytes = inputStream.available();

                    userAssetImageFileRepository.upload(userId, assetId, inputStream, aVoid -> {
                        // Uploaded.

                        // Update the status.
                        userActorImage.fileUploaded = true;
                        userAssetImageRepository.save(userActorImage);

                        // Delete the task.
                        userAssetImageFileUploadTaskRepository.delete(userId, assetId);

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
