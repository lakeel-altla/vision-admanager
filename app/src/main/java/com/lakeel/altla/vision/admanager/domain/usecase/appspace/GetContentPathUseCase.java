package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;

import javax.inject.Inject;

import rx.Single;

public final class GetContentPathUseCase {

    @Inject
    AppContentRepository appContentRepository;

    @Inject
    public GetContentPathUseCase() {
    }

    public Single<String> execute(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return appContentRepository.getFilePath(uuid);
    }
}
