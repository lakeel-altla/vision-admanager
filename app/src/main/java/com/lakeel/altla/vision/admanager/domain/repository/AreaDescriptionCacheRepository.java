package com.lakeel.altla.vision.admanager.domain.repository;

import java.io.File;

import rx.Single;

public interface AreaDescriptionCacheRepository {

    Single<File> getDirectory();

    Single<File> getFile(String id);
}
