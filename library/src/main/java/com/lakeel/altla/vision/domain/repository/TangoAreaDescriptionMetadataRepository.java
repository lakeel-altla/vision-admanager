package com.lakeel.altla.vision.domain.repository;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import rx.Completable;
import rx.Observable;

public interface TangoAreaDescriptionMetadataRepository {

    Observable<TangoAreaDescriptionMetaData> find(Tango tango, String areaDescriptionId);

    Observable<TangoAreaDescriptionMetaData> findAll(Tango tango);

    Completable delete(Tango tango, String areaDescriptionId);
}
