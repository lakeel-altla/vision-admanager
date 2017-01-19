package com.lakeel.altla.vision.data.repository.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import rx.Single;

public final class FileBitmapRepository {

    public Single<Bitmap> find(File file) {
        return Single.create(subscriber -> {
            try (FileInputStream stream = new FileInputStream(file)) {
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                if (bitmap == null) {
                    throw new IllegalStateException(String.format("Failed to decode the file stream: file = %s", file));
                }

                subscriber.onSuccess(bitmap);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }
}
