package com.lakeel.altla.vision.domain.repository;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import rx.Completable;
import rx.Observable;

public interface TangoAreaDescriptionMetadataRepository {

    Observable<TangoAreaDescriptionMetaData> find(String areaDescriptionId);

    Observable<TangoAreaDescriptionMetaData> findAll();

    Completable delete(String areaDescriptionId);
}
