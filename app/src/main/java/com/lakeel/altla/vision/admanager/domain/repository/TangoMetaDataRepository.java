package com.lakeel.altla.vision.admanager.domain.repository;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;

import rx.Observable;
import rx.Single;

public interface TangoMetadataRepository {

    Observable<AreaDescriptionMetadata> find(String uuid);

    Observable<AreaDescriptionMetadata> findAll();

    Single<String> delete(String uuid);
}
