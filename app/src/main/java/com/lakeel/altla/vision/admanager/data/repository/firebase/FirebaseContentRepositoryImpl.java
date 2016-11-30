package com.lakeel.altla.vision.admanager.data.repository.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lakeel.altla.rx.firebase.storage.RxFirebaseStorageTask;
import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseContentRepository;

import java.io.InputStream;

import javax.inject.Inject;

import rx.Single;

public final class FirebaseContentRepositoryImpl implements FirebaseContentRepository {

    private static final String PATH_AREA_DESCRIPTIONS = "areaDescriptions";

    private final StorageReference baseReference;

    private final FirebaseAuth auth;

    @Inject
    public FirebaseContentRepositoryImpl(StorageReference baseReference, FirebaseAuth auth) {
        if (baseReference == null) throw new ArgumentNullException("baseReference");
        if (auth == null) throw new ArgumentNullException("auth");

        this.baseReference = baseReference;
        this.auth = auth;
    }

    @Override
    public Single<String> save(String uuid, InputStream areaDescriptionStream, OnProgressListener onProgressListener) {
        if (uuid == null) throw new ArgumentNullException("uuid");
        if (areaDescriptionStream == null) throw new ArgumentNullException("areaDescriptionStream");

        StorageReference reference = baseReference.child(resolveUserId())
                                                  .child(PATH_AREA_DESCRIPTIONS)
                                                  .child(uuid);

        UploadTask task = reference.putStream(areaDescriptionStream);

        return RxFirebaseStorageTask.asSingle(task, onProgressListener::onProgress)
                                    .map(snapshot -> uuid);
    }

    private String resolveUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }
        return user.getUid();
    }
}
