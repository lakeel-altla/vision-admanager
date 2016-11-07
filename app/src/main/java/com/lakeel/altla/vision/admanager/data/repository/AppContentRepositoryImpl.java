package com.lakeel.altla.vision.admanager.data.repository;

import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;

public final class AppContentRepositoryImpl implements AppContentRepository {

    private final File mDirectory;

    @Inject
    public AppContentRepositoryImpl(File directory) {
        mDirectory = directory;
        if (!mDirectory.exists()) {
            if (!mDirectory.mkdirs()) {
                throw new IllegalStateException("Creating a directory failed: " + mDirectory);
            }
        }
    }

    @Override
    public Single<String> getExportDirectory() {
        return Single.just(mDirectory.getPath());
    }

    @Override
    public Single<String> getFilePath(String uuid) {
        return Single.just(mDirectory.getPath() + File.separator + uuid);
    }

    @Override
    public Observable<String> findAll() {
        return Observable.from(mDirectory.listFiles())
                         .map(File::getName);
    }

    @Override
    public Single<String> delete(String uuid) {
        File file = new File(mDirectory, uuid);
        file.delete();
        return Single.just(uuid);
    }
}
