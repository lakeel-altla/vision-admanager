package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.vision.ArgumentNullException;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

public final class DocumentRepository {

    private final ContentResolver contentResolver;

    public DocumentRepository(ContentResolver contentResolver) {
        if (contentResolver == null) throw new ArgumentNullException("contentResolver");

        this.contentResolver = contentResolver;
    }

    public InputStream openStream(String uriString) throws FileNotFoundException {
        Uri uri = Uri.parse(uriString);
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return contentResolver.openInputStream(uri);
    }
}
