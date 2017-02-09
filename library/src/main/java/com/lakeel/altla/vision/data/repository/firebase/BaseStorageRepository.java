package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.storage.FirebaseStorage;

import android.support.annotation.NonNull;

public class BaseStorageRepository {

    private final FirebaseStorage storage;

    protected BaseStorageRepository(@NonNull FirebaseStorage storage) {
        this.storage = storage;
    }

    @NonNull
    protected final FirebaseStorage getStorage() {
        return storage;
    }
}
