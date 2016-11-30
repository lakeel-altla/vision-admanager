package com.lakeel.altla.vision.admanager.data.repository;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseContentRepository;

import java.io.InputStream;

import javax.inject.Inject;

import rx.Single;

public final class FirebaseContentRepositoryImpl implements FirebaseContentRepository {

    private static final Log LOG = LogFactory.getLog(FirebaseContentRepositoryImpl.class);

    private final StorageReference baseReference;

    @Inject
    public FirebaseContentRepositoryImpl(String uri, String contentPath) {
        StorageReference referenceRoot = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
        baseReference = referenceRoot.child(contentPath);
    }

    @Override
    public Single<String> save(String uuid, InputStream areaDescriptionStream, OnProgressListener onProgressListener) {
        //
        // MEMO:
        //
        // Firebase Storage の Task のコールバックは Firebase Storage 専用スレッドで呼び出される。
        // そのコールバックから RxJava の通知メソッドを呼び出すと、これもまた Firebase Storage 専用スレッドでの呼び出しとなり、
        // 後続のストリーム処理がある場合に、それらは Firebase Storage 専用スレッドで処理されてしまうことに注意する。
        //
        return Single.create(subscriber -> {
            LOG.d("Saving content to Firebase Storage...");

            StorageReference reference = baseReference.child(uuid);
            UploadTask task = reference.putStream(areaDescriptionStream);
            task.addOnSuccessListener(taskSnapshot -> subscriber.onSuccess(uuid))
                .addOnFailureListener(subscriber::onError)
                .addOnProgressListener(snapshot -> onProgressListener.onProgress(
                        snapshot.getTotalByteCount(), snapshot.getBytesTransferred()));
        });
    }
}
