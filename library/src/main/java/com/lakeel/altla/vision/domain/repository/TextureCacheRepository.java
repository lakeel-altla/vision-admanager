package com.lakeel.altla.vision.domain.repository;

import java.io.File;

import rx.Completable;
import rx.Observable;
import rx.Single;

public interface TextureCacheRepository {

    Observable<File> find(String textureId);

    Single<File> create(String textureId);

    Completable delete(String textureId);
}
