package com.lakeel.altla.vision.admanager.data.repository.android;

import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;

public final class AppContentRepositoryImpl implements AppContentRepository {

    private final File baseDirectory;

    @Inject
    public AppContentRepositoryImpl(File directory) {
        baseDirectory = directory;
        if (!baseDirectory.exists()) {
            if (!baseDirectory.mkdirs()) {
                throw new IllegalStateException("Creating a directory failed: " + baseDirectory);
            }
        }
    }

    @Override
    public Single<String> getExportDirectory() {
        return Single.just(baseDirectory.getPath());
    }

    @Override
    public Single<String> getFilePath(String uuid) {
        return Single.just(baseDirectory.getPath() + File.separator + uuid);
    }

    @Override
    public Observable<String> findAll() {
        return Observable.from(baseDirectory.listFiles())
                         .map(File::getName);
    }

    @Override
    public Single<String> delete(String uuid) {
        File file = new File(baseDirectory, uuid);
        file.delete();
        return Single.just(uuid);
    }
}
