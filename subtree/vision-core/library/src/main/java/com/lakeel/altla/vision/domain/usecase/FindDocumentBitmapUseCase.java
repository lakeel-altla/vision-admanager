package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.android.DocumentBitmapRepository;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class FindDocumentBitmapUseCase {

    @Inject
    DocumentBitmapRepository documentBitmapRepository;

    @Inject
    public FindDocumentBitmapUseCase() {
    }

    @NonNull
    public Single<Bitmap> execute(@NonNull Uri uri) {
        return Single.<Bitmap>create(e -> {
            Bitmap bitmap = documentBitmapRepository.find(uri);
            e.onSuccess(bitmap);
        }).subscribeOn(Schedulers.io());
    }
}
