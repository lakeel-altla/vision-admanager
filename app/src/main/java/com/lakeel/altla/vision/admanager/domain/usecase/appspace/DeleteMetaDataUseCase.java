package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetadataRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteMetadataUseCase {

    @Inject
    AppMetadataRepository appMetadataRepository;

    @Inject
    AppContentRepository appContentRepository;

    @Inject
    public DeleteMetadataUseCase() {
    }

    public Single<String> execute(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return appContentRepository.delete(uuid)
                                   .flatMap(appMetadataRepository::delete)
                                   .subscribeOn(Schedulers.io());
    }
}
