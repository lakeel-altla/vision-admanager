package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.Tango;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.mapper.TangoAreaDescriptionMapper;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllTangoAreaDescriptionsUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    public FindAllTangoAreaDescriptionsUseCase() {
    }

    public Observable<TangoAreaDescription> execute(Tango tango) {
        if (tango == null) throw new ArgumentNullException("tango");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return tangoAreaDescriptionMetadataRepository
                .findAll(tango)
                .map(TangoAreaDescriptionMapper::map)
                .subscribeOn(Schedulers.io());
    }
}
