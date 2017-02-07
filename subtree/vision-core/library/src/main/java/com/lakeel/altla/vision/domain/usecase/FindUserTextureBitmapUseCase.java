package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.data.repository.android.FileBitmapRepository;
import com.lakeel.altla.vision.data.repository.android.TextureCacheRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureFileMetadataRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

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
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserTextureBitmapUseCase() {
    }

    @NonNull
    public Single<Bitmap> execute(@NonNull String textureId, OnProgressListener onProgressListener) {
        String userId = currentUserResolver.getUserId();
        Model model = new Model(userId, textureId, onProgressListener);

        return Single.just(model)
                     .flatMap(this::ensureCacheFile)
                     .flatMap(this::findRemoteUpdateTime)
                     .flatMap(this::cacheIfOutdated)
                     .flatMap(this::loadBitmap)
                     .subscribeOn(Schedulers.io());
    }

    private Single<Model> ensureCacheFile(Model model) {
        return Single.create(e -> {
            File file = textureCacheRepository.find(model.textureId);
            if (file != null) {
                LOG.d("Found the cache: file = %s", file);
                model.cached = true;
            } else {
                file = textureCacheRepository.create(model.textureId);
                LOG.d("Created the new cache: file = %s", file);
            }

            model.cacheFile = file;

            e.onSuccess(model);
        });
    }

    private Single<Model> findRemoteUpdateTime(Model model) {
        return Single.create(e -> userTextureFileMetadataRepository.find(model.userId, model.textureId, metadata -> {
            if (metadata != null) {
                LOG.d("Found the texture metadata: textureId = %s", model.textureId);
                model.remoteUpdateTimeMillis = metadata.updateTimeMillis;
                e.onSuccess(model);
            } else {
                throw new RuntimeException(String.format(
                        "The user texture metadata not found: textureId = %s", model.textureId));
            }
        }, e::onError));
    }

    private Single<Model> cacheIfOutdated(Model model) {
        if (model.isCacheOutdated()) {
            return Single.create(e -> {
                userTextureFileRepository.download(model.userId, model.textureId, model.cacheFile, aVoid -> {
                    LOG.d("Donwloaded the texture: textureId = %s", model.textureId);
                    e.onSuccess(model);
                }, e::onError, model.onProgressListener);
            });
        } else {
            return Single.just(model)
                         .doOnSuccess(_model -> LOG.d("The cache is fresh: textureId = %s", model.textureId));
        }
    }

    private Single<Bitmap> loadBitmap(Model model) {
        return Single.<Bitmap>create(e -> {
            Bitmap bitmap = fileBitmapRepository.find(model.cacheFile);
            e.onSuccess(bitmap);
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
