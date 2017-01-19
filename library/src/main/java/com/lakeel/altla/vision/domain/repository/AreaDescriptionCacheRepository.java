package com.lakeel.altla.vision.domain.repository;

import java.io.File;

import rx.Completable;
import rx.Single;

public interface AreaDescriptionCacheRepository {

    Single<Boolean> exists(String areaDescriptionId);

    Single<File> getDirectory();

    Single<File> getFile(String areaDescriptionId);

    Completable delete(String areaDescriptionId);
}
