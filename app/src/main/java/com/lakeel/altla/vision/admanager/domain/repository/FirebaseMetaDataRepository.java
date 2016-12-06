package com.lakeel.altla.vision.admanager.domain.repository;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;

import rx.Single;

public interface FirebaseMetadataRepository {

    Single<AreaDescriptionMetadata> save(AreaDescriptionMetadata metadata);
}
