package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllUserAreaDescriptionsUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    public FindAllUserAreaDescriptionsUseCase() {
    }

    public Observable<UserAreaDescription> execute() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return userAreaDescriptionRepository
                .findAll(user.getUid())
                .flatMap(this::checkFileCached)
                .flatMap(this::checkFileUploaded)
                .subscribeOn(Schedulers.io());
    }

    private Observable<UserAreaDescription> checkFileCached(UserAreaDescription userAreaDescription) {
        return areaDescriptionCacheRepository
                .exists(userAreaDescription.areaDescriptionId)
                .map(fileCached -> {
                    userAreaDescription.fileCached = fileCached;
                    return userAreaDescription;
                }).toObservable();
    }

    private Observable<UserAreaDescription> checkFileUploaded(UserAreaDescription userAreaDescription) {
        return userAreaDescriptionFileRepository
                .exists(userAreaDescription.userId, userAreaDescription.areaDescriptionId)
                .map(fileUploaded -> {
                    userAreaDescription.fileUploaded = fileUploaded;
                    return userAreaDescription;
                }).toObservable();
    }
}
