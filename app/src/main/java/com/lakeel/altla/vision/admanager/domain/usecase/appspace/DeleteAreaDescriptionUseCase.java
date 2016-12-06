package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.repository.AreaDescriptionEntryRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AreaDescriptionFileRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class DeleteAreaDescriptionUseCase {

    @Inject
    AreaDescriptionEntryRepository areaDescriptionEntryRepository;

    @Inject
    AreaDescriptionFileRepository areaDescriptionFileRepository;

    @Inject
    public DeleteAreaDescriptionUseCase() {
    }

    public Single<String> execute(String id) {
        if (id == null) throw new ArgumentNullException("id");

        return areaDescriptionFileRepository.delete(id)
                                            .flatMap(areaDescriptionEntryRepository::delete)
                                            .subscribeOn(Schedulers.io());
    }
}
