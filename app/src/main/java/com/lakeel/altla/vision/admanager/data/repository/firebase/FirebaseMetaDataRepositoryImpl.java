package com.lakeel.altla.vision.admanager.data.repository.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseMetaDataRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Single;

public final class FirebaseMetaDataRepositoryImpl implements FirebaseMetaDataRepository {

    private static final Log LOG = LogFactory.getLog(FirebaseMetaDataRepositoryImpl.class);

    private final DatabaseReference baseReference;

    @Inject
    public FirebaseMetaDataRepositoryImpl(String metaDataNode) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        baseReference = database.getReference(metaDataNode);
    }

    @Override
    public Single<AreaDescriptionMetaData> save(AreaDescriptionMetaData metaData) {
        return Single.create(subscriber -> {
            LOG.d("Saving meta data to Firebase Database...");
            FirebaseMetaData firebaseMetaData = toFirebaseMetaData(metaData);
            baseReference.child(firebaseMetaData.uuid)
                         .setValue(firebaseMetaData)
                         .addOnSuccessListener(aVoid -> subscriber.onSuccess(metaData))
                         .addOnFailureListener(subscriber::onError);
        });
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

    @IgnoreExtraProperties
    public static class FirebaseMetaData {

        public String uuid;

        public String name;

        public long date;

        public List<Double> transformationPosition;

        public List<Double> transformationRotation;
    }
}
