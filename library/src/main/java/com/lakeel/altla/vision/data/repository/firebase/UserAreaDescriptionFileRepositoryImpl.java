package com.lakeel.altla.vision.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
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
    public Single<Boolean> exists(String userId, String areaDescriptionId) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Single.<Task<StorageMetadata>>create(subscriber -> {
            StorageReference reference = rootReference.child(PATH_USER_AREA_DESCRIPTIONS)
                                                      .child(userId)
                                                      .child(areaDescriptionId);
            Task<StorageMetadata> task = reference.getMetadata();

            subscriber.onSuccess(task);
        }).flatMap(task -> Single.create(subscriber -> {
            task.addOnSuccessListener(metadata -> subscriber.onSuccess(true));
            task.addOnFailureListener(e -> {
                if (e instanceof StorageException) {
                    StorageException storageException = (StorageException) e;
                    if (storageException.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                        subscriber.onSuccess(false);
                    }
                } else {
                    subscriber.onError(e);
                }
            });
        }));
    }

    @Override
    public Completable upload(String userId, String areaDescriptionId, InputStream stream,
                              OnProgressListener onProgressListener) {
        if (userId == null) throw new ArgumentNullException("userId");
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
        if (userId == null) throw new ArgumentNullException("userId");
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
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Single.<Task<Void>>create(subscriber -> {
            StorageReference reference = rootReference.child(PATH_USER_AREA_DESCRIPTIONS)
                                                      .child(userId)
                                                      .child(areaDescriptionId);
            Task<Void> task = reference.delete();

            subscriber.onSuccess(task);
        }).flatMapCompletable(task -> Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                task.addOnSuccessListener(aVoid -> subscriber.onCompleted());
                task.addOnFailureListener(e -> {
                    if (e instanceof StorageException) {
                        StorageException storageException = (StorageException) e;
                        if (storageException.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                            subscriber.onCompleted();
                        }
                    } else {
                        subscriber.onError(e);
                    }
                });
            }
        }));
    }
}
