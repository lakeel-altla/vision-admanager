package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.ArgumentNullException;

public class BaseStorageRepository {

    private final FirebaseStorage storage;

    protected BaseStorageRepository(FirebaseStorage storage) {
        if (storage == null) throw new ArgumentNullException("storage");

        this.storage = storage;
    }

    protected final FirebaseStorage getStorage() {
        return storage;
    }

    protected final StorageReference getRootReference() {
        return storage.getReference();
    }
}
