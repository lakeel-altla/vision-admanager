package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.FileBitmapRepository;
import com.lakeel.altla.vision.data.repository.android.TextureCacheRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureFileMetadataRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class FindUserTextureBitmapUseCase {

    private static final Log LOG = LogFactory.getLog(FindUserTextureBitmapUseCase.class);

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    TextureCacheRepository textureCacheRepository;

    @Inject
    UserTextureFileRepository userTextureFileRepository;

    @Inject
    UserTextureFileMetadataRepository userTextureFileMetadataRepository;

    @Inject
    FileBitmapRepository fileBitmapRepository;

    @Inject
    public FindUserTextureBitmapUseCase() {
    }

    public Single<Bitmap> execute(String textureId, OnProgressListener onProgressListener) {
        if (textureId == null) throw new ArgumentNullException("textureId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        Model model = new Model(user.getUid(), textureId, onProgressListener);
        return Single.just(model)
                     .flatMap(this::ensureCacheFile)
                     .flatMap(this::findRemoteUpdateTime)
                     .flatMap(this::cacheIfOutdated)
                     .flatMap(this::loadBitmap)
                     .subscribeOn(Schedulers.io());
    }

    private Single<Model> ensureCacheFile(Model model) {
        return Single.create(subscriber -> {
            File file = textureCacheRepository.find(model.textureId);
            if (file != null) {
                LOG.d("Found the cache: file = %s", file);
                model.cached = true;
            } else {
                try {
                    file = textureCacheRepository.create(model.textureId);
                    LOG.d("Created the new cache: file = %s", file);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
            model.cacheFile = file;

            subscriber.onSuccess(model);
        });
    }

    private Single<Model> findRemoteUpdateTime(Model model) {
        return Single.create(subscriber -> {
            userTextureFileMetadataRepository.find(model.userId, model.textureId, metadata -> {
                if (metadata != null) {
                    LOG.d("Found the texture metadata: textureId = %s", model.textureId);
                    model.remoteUpdateTimeMillis = metadata.updateTimeMillis;
                    subscriber.onSuccess(model);
                } else {
                    subscriber.onError(new RuntimeException(String.format(
                            "The user texture metadata not found: textureId = %s",
                            model.textureId)));
                }
            }, subscriber::onError);
        });
    }

    private Single<Model> cacheIfOutdated(Model model) {
        if (model.isCacheOutdated()) {
            return Single.create(subscriber -> {
                userTextureFileRepository.download(model.userId, model.textureId, model.cacheFile, aVoid -> {
                    LOG.d("Donwloaded the texture: textureId = %s", model.textureId);
                    subscriber.onSuccess(model);
                }, subscriber::onError, model.onProgressListener);
            });
        } else {
            return Single.just(model)
                         .doOnSuccess(_model -> LOG.d("The cache is fresh: textureId = %s", model.textureId));
        }
    }

    private Single<Bitmap> loadBitmap(Model model) {
        return Single.<Bitmap>create(subscriber -> {
            try {
                Bitmap bitmap = fileBitmapRepository.find(model.cacheFile);
                subscriber.onSuccess(bitmap);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    private final class Model {

        final String userId;

        final String textureId;

        final OnProgressListener onProgressListener;

        File cacheFile;

        boolean cached;

        long remoteUpdateTimeMillis;

        Model(String userId, String textureId, OnProgressListener onProgressListener) {
            this.userId = userId;
            this.textureId = textureId;
            this.onProgressListener = onProgressListener;
        }

        boolean isCacheOutdated() {
            return !cached || cacheFile.lastModified() < remoteUpdateTimeMillis;
        }
    }
}
