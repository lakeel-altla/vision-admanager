package com.lakeel.altla.vision.admanager.domain.repository;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionEntry;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;

import rx.Observable;
import rx.Single;

public interface AreaDescriptionEntryRepository {

    Single<AreaDescriptionEntry> save(AreaDescriptionEntry entry, AreaDescriptionMetadata metadata);

    Observable<AreaDescriptionEntry> findEntry(String id);

    Observable<AreaDescriptionEntry> findAllEntries();

    Single<String> delete(String id);
}
