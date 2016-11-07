package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetaDataRepository;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class SaveMetaDataUseCase {

    @Inject
    TangoMetaDataRepository mTangoMetaDataRepository;

    @Inject
    AppMetaDataRepository mAppMetaDataRepository;

    @Inject
    public SaveMetaDataUseCase() {
    }

    public Single<String> execute(String uuid) {
        return mTangoMetaDataRepository.find(uuid)
                                       .flatMap(this::saveAreaDescriptionMetaData)
                                       .map(metaData -> uuid)
                                       .toSingle()
                                       .subscribeOn(Schedulers.io());
    }

    Observable<AreaDescriptionMetaData> saveAreaDescriptionMetaData(AreaDescriptionMetaData metaData) {
        return mAppMetaDataRepository.save(metaData).toObservable();
    }
}
