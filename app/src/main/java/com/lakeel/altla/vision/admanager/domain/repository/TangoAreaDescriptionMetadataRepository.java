package com.lakeel.altla.vision.admanager.domain.repository;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import rx.Observable;
import rx.Single;

public interface TangoAreaDescriptionMetadataRepository {

    Observable<TangoAreaDescriptionMetaData> find(String uuid);

    Observable<TangoAreaDescriptionMetaData> findAll();

    Single<String> delete(String uuid);
}
