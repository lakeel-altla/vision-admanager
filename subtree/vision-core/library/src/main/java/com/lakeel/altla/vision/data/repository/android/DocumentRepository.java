package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.vision.ArgumentNullException;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

import rx.Single;

public final class DocumentRepository {

    private final ContentResolver contentResolver;

    public DocumentRepository(ContentResolver contentResolver) {
        if (contentResolver == null) throw new ArgumentNullException("contentResolver");

        this.contentResolver = contentResolver;
    }

    public Single<InputStream> openStream(String uriString) {
        return Single.create(subscriber -> {

            Uri uri = Uri.parse(uriString);
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                InputStream inputStream = contentResolver.openInputStream(uri);
                subscriber.onSuccess(inputStream);
            } catch (FileNotFoundException e) {
                subscriber.onError(e);
            }
        });
    }
}
