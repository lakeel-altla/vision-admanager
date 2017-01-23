package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;

import java.io.File;
import java.io.InputStream;

public final class UserAreaDescriptionFileRepository extends BaseStorageRepository {

    private static final String PATH_USER_AREA_DESCRIPTIONS = "userAreaDescriptions";

    public UserAreaDescriptionFileRepository(FirebaseStorage storage) {
        super(storage);
    }

    public void exists(String userId, String areaDescriptionId, OnSuccessListener<Boolean> onSuccessListener,
                       OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        getStorage().getReference()
                    .child(PATH_USER_AREA_DESCRIPTIONS)
                    .child(userId)
                    .child(areaDescriptionId)
                    .getMetadata()
                    .addOnSuccessListener(metadata -> {
                        if (onSuccessListener != null) onSuccessListener.onSuccess(Boolean.TRUE);
                    })
                    .addOnFailureListener(e -> {
                        if (e instanceof StorageException) {
                            StorageException storageException = (StorageException) e;
                            if (storageException.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                                if (onSuccessListener != null) onSuccessListener.onSuccess(Boolean.FALSE);
                            }
                        } else {
                            if (onFailureListener != null) onFailureListener.onFailure(e);
                        }
                    });
    }

    public void upload(String userId, String areaDescriptionId, InputStream stream,
                       OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener,
                       OnProgressListener onProgressListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");
        if (stream == null) throw new ArgumentNullException("stream");

        getStorage().getReference()
                    .child(PATH_USER_AREA_DESCRIPTIONS)
                    .child(userId)
                    .child(areaDescriptionId)
                    .putStream(stream)
                    .addOnSuccessListener(snapshot -> {
                        if (onSuccessListener != null) onSuccessListener.onSuccess(null);
                    })
                    .addOnFailureListener(e -> {
                        if (onFailureListener != null) onFailureListener.onFailure(e);
                    })
                    .addOnProgressListener(snapshot -> {
                        if (onProgressListener != null) {
                            onProgressListener.onProgress(snapshot.getTotalByteCount(),
                                                          snapshot.getBytesTransferred());
                        }
                    });
    }

    public void download(String userId, String areaDescriptionId, File destination,
                         OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener,
                         OnProgressListener onProgressListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");
        if (destination == null) throw new ArgumentNullException("destination");

        getStorage().getReference()
                    .child(PATH_USER_AREA_DESCRIPTIONS)
                    .child(userId)
                    .child(areaDescriptionId)
                    .getFile(destination)
                    .addOnSuccessListener(snapshot -> {
                        if (onSuccessListener != null) onSuccessListener.onSuccess(null);
                    })
                    .addOnFailureListener(e -> {
                        if (onFailureListener != null) onFailureListener.onFailure(e);
                    })
                    .addOnProgressListener(snapshot -> {
                        if (onProgressListener != null) {
                            onProgressListener.onProgress(snapshot.getTotalByteCount(),
                                                          snapshot.getBytesTransferred());
                        }
                    });
    }

    public void delete(String userId, String areaDescriptionId, OnSuccessListener<Void> onSuccessListener,
                       OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        getStorage().getReference()
                    .child(PATH_USER_AREA_DESCRIPTIONS)
                    .child(userId)
                    .child(areaDescriptionId).delete()
                    .addOnSuccessListener(aVoid -> {
                        if (onSuccessListener != null) onSuccessListener.onSuccess(null);
                    })
                    .addOnFailureListener(e -> {
                        if (e instanceof StorageException) {
                            StorageException storageException = (StorageException) e;
                            if (storageException.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                                if (onSuccessListener != null) onSuccessListener.onSuccess(null);
                            }
                        } else {
                            if (onFailureListener != null) onFailureListener.onFailure(e);
                        }
                    });
    }
}
