package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetadataRepository;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetadataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class SaveMetadataUseCase {

    @Inject
    TangoMetadataRepository tangoMetadataRepository;

    @Inject
    AppMetadataRepository appMetadataRepository;

    @Inject
    public SaveMetadataUseCase() {
    }

    public Single<String> execute(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return tangoMetadataRepository.find(uuid)
                                      .flatMap(this::saveAreaDescriptionMetaData)
                                      .map(metadata -> uuid)
                                      .toSingle()
                                      .subscribeOn(Schedulers.io());
    }

    private Observable<AreaDescriptionMetadata> saveAreaDescriptionMetaData(AreaDescriptionMetadata metaData) {
        return appMetadataRepository.save(metaData).toObservable();
    }
}
