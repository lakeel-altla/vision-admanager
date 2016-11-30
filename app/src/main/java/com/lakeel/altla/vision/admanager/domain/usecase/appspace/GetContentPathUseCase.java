package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

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
        return appContentRepository.getFilePath(uuid);
    }
}
