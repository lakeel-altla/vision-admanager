package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.TextureCacheRepository;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Single;

public final class TextureCacheRepositoryImpl implements TextureCacheRepository {

    private static final Log LOG = LogFactory.getLog(TextureCacheRepositoryImpl.class);

    private final Context context;

    public TextureCacheRepositoryImpl(Context context) {
        if (context == null) throw new ArgumentNullException("context");

        this.context = context;
    }

    @Override
    public Observable<File> find(String textureId) {
        if (textureId == null) throw new ArgumentNullException("textureId");

        return Observable.create(subscriber -> {
            File file = resolveCacheFile(textureId);
            if (file.exists()) {
                LOG.d("The cache file exists: textureId = %s", textureId);

                subscriber.onNext(file);
            }

            subscriber.onCompleted();
        });
    }

    @Override
    public Single<File> create(String textureId) {
        if (textureId == null) throw new ArgumentNullException("textureId");

        return Single.create(subscriber -> {
            File file = resolveCacheFile(textureId);
            try {
                if (file.createNewFile()) {
                    LOG.d("Created the new cache file: textureId = %s", textureId);
                } else {
                    LOG.w("The cache file already exists: textureId = %s", textureId);
                }

                subscriber.onSuccess(file);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Completable delete(String textureId) {
        if (textureId == null) throw new ArgumentNullException("textureId");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                File file = resolveCacheFile(textureId);
                if (file.delete()) {
                    LOG.d("Deleted the new cache file: textureId = %s", textureId);
                } else {
                    LOG.w("The cache file does not exist: textureId = %s", textureId);
                }

                subscriber.onCompleted();
            }
        });
    }

    private File resolveCacheFile(String textureId) {
        File directory = new File(context.getCacheDir(), "textures");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return new File(directory, textureId);
    }
}
