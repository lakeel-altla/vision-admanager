package com.lakeel.altla.vision.admanager.domain.repository;

import java.io.InputStream;

import rx.Single;

public interface FirebaseContentRepository {

    Single<String> save(String uuid, InputStream areaDescriptionStream, OnProgressListener onProgressListener);

    interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);

    }
}
