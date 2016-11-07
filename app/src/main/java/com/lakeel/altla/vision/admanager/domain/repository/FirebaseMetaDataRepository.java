package com.lakeel.altla.vision.admanager.domain.repository;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;

import rx.Single;

public interface FirebaseMetaDataRepository {

    Single<AreaDescriptionMetaData> save(AreaDescriptionMetaData metaData);
}
