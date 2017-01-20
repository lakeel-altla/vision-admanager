package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.DocumentFilenameRepository;

import android.net.Uri;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class FindDocumentFilenameUseCase {

    @Inject
    DocumentFilenameRepository documentFilenameRepository;

    @Inject
    public FindDocumentFilenameUseCase() {
    }

    public Single<String> execute(Uri uri) {
        if (uri == null) throw new ArgumentNullException("uri");

        return Single.<String>create(e -> {
            String filename = documentFilenameRepository.find(uri);
            e.onSuccess(filename);
        }).subscribeOn(Schedulers.io());
    }
}
