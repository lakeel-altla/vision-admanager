package com.lakeel.altla.vision.admanager.data.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseMetaDataRepository;

import javax.inject.Inject;

import rx.Single;

public final class FirebaseMetaDataRepositoryImpl implements FirebaseMetaDataRepository {

    private static final Log LOG = LogFactory.getLog(FirebaseMetaDataRepositoryImpl.class);

    private final MetaDataMapper mMapper = new MetaDataMapper();

    private final DatabaseReference mReferenceMetaData;

    @Inject
    public FirebaseMetaDataRepositoryImpl(String metaDataNode) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mReferenceMetaData = database.getReference(metaDataNode);
    }

    @Override
    public Single<AreaDescriptionMetaData> save(AreaDescriptionMetaData metaData) {
        //
        // MEMO:
        //
        // Firebase Database の Task のコールバックは Main スレッドで呼び出される。
        // そのコールバックから RxJava の通知メソッドを呼び出すと、これもまた Main スレッドでの呼び出しとなり、
        // 後続のストリーム処理がある場合に、それらは Main スレッドで処理されてしまうことに注意する。
        //
        return Single.create(subscriber -> {
            LOG.d("Saving meta data to Firebase Database...");
            FirebaseMetaData firebaseMetaData = mMapper.toFirebaseMetaData(metaData);
            mReferenceMetaData.child(firebaseMetaData.uuid)
                              .setValue(firebaseMetaData)
                              .addOnSuccessListener(aVoid -> subscriber.onSuccess(metaData))
                              .addOnFailureListener(subscriber::onError);
        });
    }
}
