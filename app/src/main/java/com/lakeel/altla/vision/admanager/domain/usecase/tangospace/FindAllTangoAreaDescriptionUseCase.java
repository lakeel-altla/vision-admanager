package com.lakeel.altla.vision.admanager.domain.usecase.tangospace;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionEntry;
import com.lakeel.altla.vision.admanager.domain.repository.TangoAreaDescriptionMetadataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllTangoAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    public FindAllTangoAreaDescriptionUseCase() {
    }

    public Observable<AreaDescriptionEntry> execute() {
        return tangoAreaDescriptionMetadataRepository
                .findAll()
                .map(tangoAreaDescriptionMetaData -> {
                    String id = TangoAreaDescriptionMetaDataHelper.getUuid(tangoAreaDescriptionMetaData);
                    String name = TangoAreaDescriptionMetaDataHelper.getName(tangoAreaDescriptionMetaData);
                    return new AreaDescriptionEntry(id, name);
                })
                .subscribeOn(Schedulers.io());
    }
}
