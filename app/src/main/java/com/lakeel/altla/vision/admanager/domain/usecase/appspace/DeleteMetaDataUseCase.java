package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetaDataRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteMetaDataUseCase {

    @Inject
    AppMetaDataRepository mMetaDataRepository;

    @Inject
    AppContentRepository mContentRepository;

    @Inject
    public DeleteMetaDataUseCase() {
    }

    public Single<String> execute(String uuid) {
        return mContentRepository.delete(uuid)
                                 .flatMap(mMetaDataRepository::delete)
                                 .subscribeOn(Schedulers.io());
    }
}
