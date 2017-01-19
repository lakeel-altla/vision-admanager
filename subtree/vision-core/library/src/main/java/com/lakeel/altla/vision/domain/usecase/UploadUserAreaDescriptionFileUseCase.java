package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public final class UploadUserAreaDescriptionFileUseCase {

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    private final Action1<? super InputStream> closeStream = stream -> {
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    @Inject
    public UploadUserAreaDescriptionFileUseCase() {
    }

    public Completable execute(String areaDescriptionId, OnProgressListener onProgressListener) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        // Convert arguments to the internal model.
        return Single.just(new Model(user.getUid(), areaDescriptionId, onProgressListener))
                     // Open the stream of the area description file as cache.
                     .flatMap(this::createCacheStream)
                     // Get the total bytes of it.
                     .flatMap(this::getTotalBytes)
                     // Upload it to Firebase Storage.
                     .flatMap(this::uploadUserAreaDescriptionFile)
                     .toCompletable()
                     .subscribeOn(Schedulers.io());
    }

    private Single<Model> createCacheStream(Model model) {
        return Single.create(subscriber -> {
            File file = areaDescriptionCacheRepository.getFile(model.areaDescriptionId);
            try {
                model.stream = new FileInputStream(file);
                subscriber.onSuccess(model);
            } catch (FileNotFoundException e) {
                subscriber.onError(e);
            }
        });
    }

    private Single<Model> getTotalBytes(Model model) {
        return Single.<Long>create(subscriber -> {
            try {
                long totalBytes = model.stream.available();
                subscriber.onSuccess(totalBytes);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }).map(totalBytes -> {
            model.totalBytes = totalBytes;
            return model;
        });
    }

    private Single<Model> uploadUserAreaDescriptionFile(
            Model model) {
        return Completable
                .using(() -> model.stream,
                       stream -> Completable.create(new Completable.OnSubscribe() {
                           @Override
                           public void call(CompletableSubscriber subscriber) {
                               userAreaDescriptionFileRepository.upload(
                                       model.userId, model.areaDescriptionId, model.stream,
                                       aVoid -> subscriber.onCompleted(),
                                       subscriber::onError,
                                       (totalBytes, bytesTransferred) ->
                                               model.onProgressListener.onProgress(model.totalBytes, bytesTransferred));
                           }
                       }), closeStream)
                .toSingleDefault(model);
    }

    private final class Model {

        final String userId;

        final String areaDescriptionId;

        final OnProgressListener onProgressListener;

        InputStream stream;

        long totalBytes;

        Model(String userId, String areaDescriptionId, OnProgressListener onProgressListener) {
            this.userId = userId;
            this.areaDescriptionId = areaDescriptionId;
            this.onProgressListener = onProgressListener;
        }
    }
}
