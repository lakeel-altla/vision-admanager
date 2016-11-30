package com.lakeel.altla.vision.admanager.domain.usecase.tangospace;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllMetaDatasUseCase {

    private final TangoMetaDataRepository tangoMetaDataRepository;

    @Inject
    public FindAllMetaDatasUseCase(TangoMetaDataRepository repository) {
        tangoMetaDataRepository = repository;
    }

    public Observable<AreaDescriptionMetaData> execute() {
        return tangoMetaDataRepository.findAll()
                                      .subscribeOn(Schedulers.io());
    }
}
