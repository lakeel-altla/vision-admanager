package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadataComparators;
import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetadataRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllMetadatasUseCase {

    @Inject
    AppMetadataRepository appMetadataRepository;

    @Inject
    AppContentRepository appContentRepository;

    @Inject
    public FindAllMetadatasUseCase() {
    }

    public Observable<AreaDescriptionMetadata> execute() {
        return appContentRepository.findAll()
                                   .flatMap(this::findAreaDescriptionMetaData)
                                   .toList()
                                   .flatMap(this::sortAreaDescriptionMetaDataList)
                                   .flatMap(Observable::from)
                                   .subscribeOn(Schedulers.io());
    }

    private Observable<AreaDescriptionMetadata> findAreaDescriptionMetaData(String uuid) {
        return appMetadataRepository.find(uuid);
    }

    private Observable<List<AreaDescriptionMetadata>> sortAreaDescriptionMetaDataList(
            List<AreaDescriptionMetadata> metadatas) {
        Collections.sort(metadatas, AreaDescriptionMetadataComparators.DATE_COMPARATOR);
        return Observable.just(metadatas);
    }
}
