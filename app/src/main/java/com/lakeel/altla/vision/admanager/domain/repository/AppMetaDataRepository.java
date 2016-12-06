package com.lakeel.altla.vision.admanager.domain.repository;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;

import rx.Observable;
import rx.Single;

public interface AppMetadataRepository {

    Observable<AreaDescriptionMetadata> find(String uuid);

    Single<AreaDescriptionMetadata> save(AreaDescriptionMetadata metadata);

    Single<String> delete(String uuid);
}
