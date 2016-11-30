package com.lakeel.altla.vision.admanager.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;

import com.lakeel.altla.rx.tasks.RxGmsTask;
import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseMetaDataRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Single;

public final class FirebaseMetaDataRepositoryImpl implements FirebaseMetaDataRepository {

    private static final String PATH_AREA_DESCRIPTION_METADATAS = "areaDescriptionMetadatas";

    private final DatabaseReference baseReference;

    private final FirebaseAuth auth;

    @Inject
    public FirebaseMetaDataRepositoryImpl(DatabaseReference baseReference, FirebaseAuth auth) {
        if (baseReference == null) throw new ArgumentNullException("baseReference");
        if (auth == null) throw new ArgumentNullException("auth");

        this.baseReference = baseReference;
        this.auth = auth;
    }

    @Override
    public Single<AreaDescriptionMetaData> save(AreaDescriptionMetaData metaData) {
        if (metaData == null) throw new ArgumentNullException("metaData");

        FirebaseMetaData firebaseMetaData = toFirebaseMetaData(metaData);

        Task<Void> task = baseReference.child(resolveUserId())
                                       .child(PATH_AREA_DESCRIPTION_METADATAS)
                                       .child(firebaseMetaData.uuid)
                                       .setValue(firebaseMetaData);

        return RxGmsTask.asSingle(task)
                        .map(aVoid -> metaData);
    }

    private static FirebaseMetaData toFirebaseMetaData(AreaDescriptionMetaData metaData) {
        FirebaseMetaData firebaseMetaData = new FirebaseMetaData();

        firebaseMetaData.uuid = metaData.uuid;
        firebaseMetaData.name = metaData.name;

        if (metaData.date != null) {
            firebaseMetaData.date = metaData.date.getTime();
        }

        if (metaData.transformationPosition == null) {
            firebaseMetaData.transformationPosition = Collections.emptyList();
        } else {
            firebaseMetaData.transformationPosition = new ArrayList<>(metaData.transformationPosition.length);
            for (double value : metaData.transformationPosition) {
                firebaseMetaData.transformationPosition.add(value);
            }
        }

        if (metaData.transformationRotation == null) {
            firebaseMetaData.transformationRotation = Collections.emptyList();
        } else {
            firebaseMetaData.transformationRotation = new ArrayList<>(metaData.transformationRotation.length);
            for (double value : metaData.transformationRotation) {
                firebaseMetaData.transformationRotation.add(value);
            }
        }

        return firebaseMetaData;
    }

    private String resolveUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The current user could not be resolved.");
        }
        return user.getUid();
    }

    @IgnoreExtraProperties
    public static class FirebaseMetaData {

        public String uuid;

        public String name;

        public long date;

        public List<Double> transformationPosition;

        public List<Double> transformationRotation;
    }
}
