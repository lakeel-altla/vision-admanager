package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;

import java.io.File;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single;

public final class AreaDescriptionCacheRepositoryImpl implements AreaDescriptionCacheRepository {

    private static final String PATH = "areaDescriptions";

    private final File rootDirectory;

    public AreaDescriptionCacheRepositoryImpl(File rootDirectory) {
        if (rootDirectory == null) throw new ArgumentNullException("rootDirectory");

        this.rootDirectory = rootDirectory;
    }

    @Override
    public Single<Boolean> exists(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Single.create(subscriber -> {
            File file = resolveCacheFile(areaDescriptionId);
            subscriber.onSuccess(file.exists());
        });
    }

    @Override
    public Single<File> getDirectory() {
        return Single.create(subscriber -> {
            File file = ensureCacheDirectory();
            subscriber.onSuccess(file);
        });
    }

    @Override
    public Single<File> getFile(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Single.create(subscriber -> {
            File file = resolveCacheFile(areaDescriptionId);
            subscriber.onSuccess(file);
        });
    }

    @Override
    public Completable delete(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                File file = resolveCacheFile(areaDescriptionId);
                if (file.exists()) {
                    file.delete();
                }
                subscriber.onCompleted();
            }
        });
    }

    private File ensureCacheDirectory() {
        File directory = new File(rootDirectory, PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private File resolveCacheFile(String areaDescriptionId) {
        File directory = ensureCacheDirectory();
        return new File(directory, areaDescriptionId);
    }
}
