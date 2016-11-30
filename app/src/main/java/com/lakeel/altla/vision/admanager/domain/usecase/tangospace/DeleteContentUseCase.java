package com.lakeel.altla.vision.admanager.domain.usecase.tangospace;

import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteContentUseCase {

    private final TangoMetaDataRepository tangoMetaDataRepository;

    @Inject
    public DeleteContentUseCase(TangoMetaDataRepository repository) {
        tangoMetaDataRepository = repository;
    }

    public Single<String> execute(String uuid) {
        return tangoMetaDataRepository.delete(uuid)
                                      .subscribeOn(Schedulers.io());
    }
}
