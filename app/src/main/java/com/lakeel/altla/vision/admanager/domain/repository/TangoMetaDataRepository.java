package com.lakeel.altla.vision.admanager.domain.repository;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;

import rx.Observable;
import rx.Single;

public interface TangoMetaDataRepository {

    Observable<AreaDescriptionMetaData> find(String uuid);

    Observable<AreaDescriptionMetaData> findAll();

    Single<String> delete(String uuid);
}
