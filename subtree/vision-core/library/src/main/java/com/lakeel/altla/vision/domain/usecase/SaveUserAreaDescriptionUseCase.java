package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import javax.inject.Inject;

import rx.Completable;
import rx.schedulers.Schedulers;

public final class SaveUserAreaDescriptionUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    public SaveUserAreaDescriptionUseCase() {
    }

    public Completable execute(UserAreaDescription userAreaDescription) {
        if (userAreaDescription == null) throw new ArgumentNullException("areaDescriptionId");

        return userAreaDescriptionRepository
                .save(userAreaDescription)
                .subscribeOn(Schedulers.io());
    }
}
