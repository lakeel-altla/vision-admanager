package com.lakeel.altla.vision.admanager.domain.usecase.tangospace;

import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteContentUseCase {

    private final TangoMetaDataRepository mRepository;

    @Inject
    public DeleteContentUseCase(TangoMetaDataRepository repository) {
        mRepository = repository;
    }

    public Single<String> execute(String uuid) {
        return mRepository.delete(uuid)
                          .subscribeOn(Schedulers.io());
    }
}
