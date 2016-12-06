package com.lakeel.altla.vision.admanager.domain.usecase.tangospace;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetadataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllMetaDatasUseCase {

    @Inject
    TangoMetadataRepository tangoMetadataRepository;

    @Inject
    public FindAllMetaDatasUseCase() {
    }

    public Observable<AreaDescriptionMetadata> execute() {
        return tangoMetadataRepository.findAll()
                                      .subscribeOn(Schedulers.io());
    }
}
