package com.lakeel.altla.vision.admanager.domain.repository;

import rx.Observable;
import rx.Single;

public interface AppContentRepository {

    Single<String> getExportDirectory();

    Single<String> getFilePath(String uuid);

    Observable<String> findAll();

    Single<String> delete(String uuid);
}
