package com.lakeel.altla.vision.admanager.domain.usecase.tangospace;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetadataRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteContentUseCase {

    @Inject
    TangoMetadataRepository tangoMetadataRepository;

    @Inject
    public DeleteContentUseCase() {
    }

    public Single<String> execute(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return tangoMetadataRepository.delete(uuid)
                                      .subscribeOn(Schedulers.io());
    }
}
