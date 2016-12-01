package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetaDataRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteMetaDataUseCase {

    @Inject
    AppMetaDataRepository appMetaDataRepository;

    @Inject
    AppContentRepository appContentRepository;

    @Inject
    public DeleteMetaDataUseCase() {
    }

    public Single<String> execute(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return appContentRepository.delete(uuid)
                                   .flatMap(appMetaDataRepository::delete)
                                   .subscribeOn(Schedulers.io());
    }
}
