package com.lakeel.altla.vision.data.repository.android;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import rx.Single;

public final class DocumentFilenameRepository {

    private final ContentResolver contentResolver;

    public DocumentFilenameRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public Single<String> find(Uri uri) {
        return Single.create(subscriber -> {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor == null) {
                throw new IllegalStateException(
                        String.format("Can not get a cursor to resolve the filename: uri = %s", uri));
            }

            int index = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);

            String filename = null;
            try {
                if (cursor.moveToFirst()) {
                    filename = cursor.getString(index);
                }
            } finally {
                cursor.close();
            }

            subscriber.onSuccess(filename);
        });
    }
}
