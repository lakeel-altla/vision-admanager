package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaDataComparators;
import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetaDataRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllMetaDatasUseCase {

    @Inject
    AppMetaDataRepository appMetaDataRepository;

    @Inject
    AppContentRepository appContentRepository;

    @Inject
    public FindAllMetaDatasUseCase() {
    }

    public Observable<AreaDescriptionMetaData> execute() {
        return appContentRepository.findAll()
                                   .flatMap(this::findAreaDescriptionMetaData)
                                   .toList()
                                   .flatMap(this::sortAreaDescriptionMetaDataList)
                                   .flatMap(Observable::from)
                                   .subscribeOn(Schedulers.io());
    }

    private Observable<AreaDescriptionMetaData> findAreaDescriptionMetaData(String uuid) {
        return appMetaDataRepository.find(uuid);
    }

    private Observable<List<AreaDescriptionMetaData>> sortAreaDescriptionMetaDataList(
            List<AreaDescriptionMetaData> metaDatas) {
        Collections.sort(metaDatas, AreaDescriptionMetaDataComparators.DATE_COMPARATOR);
        return Observable.just(metaDatas);
    }
}
