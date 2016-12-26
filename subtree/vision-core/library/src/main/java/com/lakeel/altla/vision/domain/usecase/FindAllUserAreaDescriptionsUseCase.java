package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.domain.mapper.UserAreaDescriptionMapper;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllUserAreaDescriptionsUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    public FindAllUserAreaDescriptionsUseCase() {
    }

    public Observable<UserAreaDescription> execute() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return tangoAreaDescriptionMetadataRepository
                // Find all area descriptions that are stored in Tango.
                .findAll()
                // Map it to a model for internal use.
                .map(metaData -> UserAreaDescriptionMapper.map(user.getUid(), metaData))
                // Check whether it is synchronized with the server.
                .flatMap(this::resolveAreaDescription)
                .subscribeOn(Schedulers.io());
    }

    private Observable<UserAreaDescription> resolveAreaDescription(UserAreaDescription tangoAreaDescroption) {
        return userAreaDescriptionRepository
                .find(tangoAreaDescroption.userId, tangoAreaDescroption.areaDescriptionId)
                .map(userAreaDescription -> {
                    // Mark as synced.
                    userAreaDescription.synced = true;
                    return userAreaDescription;
                })
                .defaultIfEmpty(tangoAreaDescroption);
    }
}
