package com.lakeel.altla.vision.admanager.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;

import com.lakeel.altla.rx.tasks.RxGmsTask;
import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseMetadataRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Single;

public final class FirebaseMetadataRepositoryImpl implements FirebaseMetadataRepository {

    private static final String PATH_AREA_DESCRIPTION_METADATAS = "areaDescriptionMetadatas";

    private final DatabaseReference baseReference;

    private final FirebaseAuth auth;

    @Inject
    public FirebaseMetadataRepositoryImpl(DatabaseReference baseReference, FirebaseAuth auth) {
        if (baseReference == null) throw new ArgumentNullException("baseReference");
        if (auth == null) throw new ArgumentNullException("auth");

        this.baseReference = baseReference;
        this.auth = auth;
    }

    @Override
    public Single<AreaDescriptionMetadata> save(AreaDescriptionMetadata metadata) {
        if (metadata == null) throw new ArgumentNullException("metadata");

        FirebaseMetadata firebaseMetadata = toFirebaseMetaData(metadata);

        Task<Void> task = baseReference.child(resolveUserId())
                                       .child(PATH_AREA_DESCRIPTION_METADATAS)
                                       .child(firebaseMetadata.uuid)
                                       .setValue(firebaseMetadata);

        return RxGmsTask.asSingle(task)
                        .map(aVoid -> metadata);
    }

    private static FirebaseMetadata toFirebaseMetaData(AreaDescriptionMetadata metadata) {
        FirebaseMetadata firebaseMetadata = new FirebaseMetadata();

        firebaseMetadata.uuid = metadata.uuid;
        firebaseMetadata.name = metadata.name;

        if (metadata.date != null) {
            firebaseMetadata.date = metadata.date.getTime();
        }

        if (metadata.transformationPosition == null) {
            firebaseMetadata.transformationPosition = Collections.emptyList();
        } else {
            firebaseMetadata.transformationPosition = new ArrayList<>(metadata.transformationPosition.length);
            for (double value : metadata.transformationPosition) {
                firebaseMetadata.transformationPosition.add(value);
            }
        }

        if (metadata.transformationRotation == null) {
            firebaseMetadata.transformationRotation = Collections.emptyList();
        } else {
            firebaseMetadata.transformationRotation = new ArrayList<>(metadata.transformationRotation.length);
            for (double value : metadata.transformationRotation) {
                firebaseMetadata.transformationRotation.add(value);
            }
        }

        return firebaseMetadata;
    }

    private String resolveUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }
        return user.getUid();
    }

    @IgnoreExtraProperties
    public static class FirebaseMetadata {

        public String uuid;

        public String name;

        public long date;

        public List<Double> transformationPosition;

        public List<Double> transformationRotation;
    }
}
