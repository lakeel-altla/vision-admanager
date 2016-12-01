package com.lakeel.altla.vision.admanager.domain.usecase.tangospace;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteContentUseCase {

    @Inject
    TangoMetaDataRepository tangoMetaDataRepository;

    @Inject
    public DeleteContentUseCase() {
    }

    public Single<String> execute(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return tangoMetaDataRepository.delete(uuid)
                                      .subscribeOn(Schedulers.io());
    }
}
