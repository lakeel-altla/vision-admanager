package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;
import com.lakeel.altla.vision.domain.repository.FileBitmapRepository;
import com.lakeel.altla.vision.domain.repository.TextureCacheRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureFileMetadataRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureFileRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import android.graphics.Bitmap;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
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
        return textureCacheRepository
                .find(model.textureId)
                .doOnNext(file -> LOG.d("Found the cache: file = %s", file))
                .map(file -> {
                    model.cacheFile = file;
                    model.cached = true;
                    return model;
                }).switchIfEmpty(createCacheFile(model).toObservable())
                .toSingle();
    }

    private Single<Model> createCacheFile(Model model) {
        return textureCacheRepository
                .create(model.textureId)
                .doOnSuccess(file -> LOG.d("Created the new cache: file = %s", file))
                .map(file -> {
                    model.cacheFile = file;
                    return model;
                });
    }

    private Single<Model> findRemoteUpdateTime(Model model) {
        return userTextureFileMetadataRepository
                .find(model.userId, model.textureId)
                .doOnNext(metadata -> LOG.d("Found the texture metadata: textureId = %s", model.textureId))
                .map(metadata -> {
                    model.remoteUpdateTimeMillis = metadata.updateTimeMillis;
                    return model;
                })
                .switchIfEmpty(Observable.create(subscriber -> {
                    // TODO
                    subscriber.onError(new RuntimeException(String.format(
                            "The user texture metadata not found: textureId = %s",
                            model.textureId)));
                }))
                .toSingle();
    }

    private Single<Model> cacheIfOutdated(Model model) {
        if (model.isCacheOutdated()) {
            return userTextureFileRepository
                    .download(model.userId, model.textureId, model.cacheFile, model.onProgressListener)
                    .doOnCompleted(() -> LOG.d("Donwloaded the texture: textureId = %s", model.textureId))
                    .toSingleDefault(model);
        } else {
            return Single.just(model)
                         .doOnSuccess(_model -> LOG.d("The cache is fresh: textureId = %s", model.textureId));
        }
    }

    private Single<Bitmap> loadBitmap(Model model) {
        return fileBitmapRepository.find(model.cacheFile)
                                   .subscribeOn(Schedulers.io());
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
