package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetaDataRepository;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class SaveMetaDataUseCase {

    @Inject
    TangoMetaDataRepository tangoMetaDataRepository;

    @Inject
    AppMetaDataRepository appMetaDataRepository;

    @Inject
    public SaveMetaDataUseCase() {
    }

    public Single<String> execute(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return tangoMetaDataRepository.find(uuid)
                                      .flatMap(this::saveAreaDescriptionMetaData)
                                      .map(metaData -> uuid)
                                      .toSingle()
                                      .subscribeOn(Schedulers.io());
    }

    private Observable<AreaDescriptionMetaData> saveAreaDescriptionMetaData(AreaDescriptionMetaData metaData) {
        return appMetaDataRepository.save(metaData).toObservable();
    }
}
