package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;

import javax.inject.Inject;

import rx.Single;

public final class GetContentDirectoryUseCase {

    @Inject
    AppContentRepository appContentRepository;

    @Inject
    public GetContentDirectoryUseCase() {
    }

    public Single<String> execute() {
        return appContentRepository.getExportDirectory();
    }
}
