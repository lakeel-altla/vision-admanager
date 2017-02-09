package com.lakeel.altla.vision.data.repository.android;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStream;

public final class DocumentRepository {

    private final ContentResolver contentResolver;

    public DocumentRepository(@NonNull ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Nullable
    public InputStream openStream(@NonNull String uriString) throws FileNotFoundException {
        Uri uri = Uri.parse(uriString);
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return contentResolver.openInputStream(uri);
    }
}
