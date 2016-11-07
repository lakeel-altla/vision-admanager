package com.lakeel.altla.vision.admanager.domain.repository;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;

import rx.Observable;
import rx.Single;

public interface AppMetaDataRepository {

    Observable<AreaDescriptionMetaData> find(String uuid);

    Single<AreaDescriptionMetaData> save(AreaDescriptionMetaData metaData);

    Single<String> delete(String uuid);
}
