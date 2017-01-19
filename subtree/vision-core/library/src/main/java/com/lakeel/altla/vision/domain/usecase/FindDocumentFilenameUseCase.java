package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.DocumentFilenameRepository;

import android.net.Uri;

import javax.inject.Inject;

import rx.Single;

public final class FindDocumentFilenameUseCase {

    @Inject
    DocumentFilenameRepository documentFilenameRepository;

    @Inject
    public FindDocumentFilenameUseCase() {
    }

    public Single<String> execute(Uri uri) {
        if (uri == null) throw new ArgumentNullException("uri");

        return Single.create(subscriber -> {
            String filename = documentFilenameRepository.find(uri);
            subscriber.onSuccess(filename);
        });
    }
}
