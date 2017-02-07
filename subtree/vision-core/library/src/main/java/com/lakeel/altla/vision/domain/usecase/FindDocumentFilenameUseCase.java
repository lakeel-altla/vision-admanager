package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.android.DocumentFilenameRepository;

import android.net.Uri;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class FindDocumentFilenameUseCase {

    @Inject
    DocumentFilenameRepository documentFilenameRepository;

    @Inject
    public FindDocumentFilenameUseCase() {
    }

    @NonNull
    public Single<String> execute(@NonNull Uri uri) {
        return Single.<String>create(e -> {
            String filename = documentFilenameRepository.find(uri);
            e.onSuccess(filename);
        }).subscribeOn(Schedulers.io());
    }
}
