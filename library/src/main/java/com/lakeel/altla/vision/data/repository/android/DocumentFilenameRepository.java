package com.lakeel.altla.vision.data.repository.android;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public final class DocumentFilenameRepository {

    private final ContentResolver contentResolver;

    public DocumentFilenameRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public String find(Uri uri) {
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            throw new IllegalStateException(
                    String.format("Can not get a cursor to resolve the filename: uri = %s", uri));
        }

        int index = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(index);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }
}
