package com.lakeel.altla.vision.admanager.domain.usecase.tangospace;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllMetaDatasUseCase {

    private final TangoMetaDataRepository mRepository;

    @Inject
    public FindAllMetaDatasUseCase(TangoMetaDataRepository repository) {
        mRepository = repository;
    }

    public Observable<AreaDescriptionMetaData> execute() {
        return mRepository.findAll()
                          .subscribeOn(Schedulers.io());
    }
}
