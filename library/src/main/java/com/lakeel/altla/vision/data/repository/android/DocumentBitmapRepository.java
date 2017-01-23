package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.vision.ArgumentNullException;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.IOException;

public final class DocumentBitmapRepository {

    private final ContentResolver contentResolver;

    public DocumentBitmapRepository(ContentResolver contentResolver) {
        if (contentResolver == null) throw new ArgumentNullException("contentResolver");

        this.contentResolver = contentResolver;
    }

    public Bitmap find(Uri uri) throws IOException {
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try (ParcelFileDescriptor descriptor = contentResolver.openFileDescriptor(uri, "r")) {
            if (descriptor == null) {
                throw new IllegalStateException("Failed to open the file descriptor: uri = " + uri);
            }

            FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            if (bitmap != null) {
                return bitmap;
            } else {
                throw new IllegalStateException("Failed to decode the file descriptor: uri = " + uri);
            }
        }
    }
}
