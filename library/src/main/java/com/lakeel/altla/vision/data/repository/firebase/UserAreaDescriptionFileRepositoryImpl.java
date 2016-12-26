package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lakeel.altla.rx.firebase.storage.RxFirebaseStorageTask;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionFileRepository;

import java.io.File;
import java.io.InputStream;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single;

public final class UserAreaDescriptionFileRepositoryImpl implements UserAreaDescriptionFileRepository {

    private static final String PATH_USER_AREA_DESCRIPTIONS = "userAreaDescriptions";

    private final StorageReference rootReference;

    public UserAreaDescriptionFileRepositoryImpl(StorageReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    @Override
    public Completable upload(String userId, String areaDescriptionId, InputStream stream,
                              OnProgressListener onProgressListener) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");
        if (stream == null) throw new ArgumentNullException("stream");

        return Single.<UploadTask>create(subscriber -> {
            StorageReference reference = rootReference.child(PATH_USER_AREA_DESCRIPTIONS)
                                                      .child(userId)
                                                      .child(areaDescriptionId);
            UploadTask task = reference.putStream(stream);

            subscriber.onSuccess(task);
        }).flatMap(task -> RxFirebaseStorageTask.asSingle(task, onProgressListener::onProgress))
          .toCompletable();
    }

    @Override
    public Completable download(String userId, String areaDescriptionId, File destination,
                                OnProgressListener onProgressListener) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");
        if (destination == null) throw new ArgumentNullException("destination");

        return Single.<FileDownloadTask>create(subscriber -> {
            StorageReference reference = rootReference.child(PATH_USER_AREA_DESCRIPTIONS)
                                                      .child(userId)
                                                      .child(areaDescriptionId);
            FileDownloadTask task = reference.getFile(destination);

            subscriber.onSuccess(task);
        }).flatMap(task -> RxFirebaseStorageTask.asSingle(task, onProgressListener::onProgress))
          .toCompletable();
    }

    @Override
    public Completable delete(String userId, String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                StorageReference reference = rootReference.child(PATH_USER_AREA_DESCRIPTIONS)
                                                          .child(userId)
                                                          .child(areaDescriptionId);
                reference.delete();

                subscriber.onCompleted();
            }
        });
    }
}
