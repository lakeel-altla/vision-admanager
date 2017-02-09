package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.storage.FirebaseStorage;

import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.InputStream;

public final class UserTextureFileRepository extends BaseStorageRepository {

    private static final String PATH_USER_TEXTURES = "userTextures";

    public UserTextureFileRepository(@NonNull FirebaseStorage storage) {
        super(storage);
    }

    public void save(@NonNull String userId, @NonNull String textureId, @NonNull InputStream stream,
                     OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener,
                     OnProgressListener onProgressListener) {
        getStorage().getReference()
                    .child(PATH_USER_TEXTURES)
                    .child(userId)
                    .child(textureId)
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

    public void delete(@NonNull String userId, @NonNull String textureId, OnSuccessListener<Void> onSuccessListener,
                       OnFailureListener onFailureListener) {
        getStorage().getReference()
                    .child(PATH_USER_TEXTURES)
                    .child(userId)
                    .child(textureId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        if (onSuccessListener != null) onSuccessListener.onSuccess(null);
                    })
                    .addOnFailureListener(e -> {
                        if (onFailureListener != null) onFailureListener.onFailure(e);
                    });
    }

    public void download(@NonNull String userId, @NonNull String textureId, @NonNull File destination,
                         OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener,
                         OnProgressListener onProgressListener) {
        getStorage().getReference()
                    .child(PATH_USER_TEXTURES)
                    .child(userId)
                    .child(textureId)
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
}
