package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

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

        String userId = user.getUid();

        return userAreaDescriptionRepository
                .findAll(userId)
                .flatMap(this::checkFileCached)
                .flatMap(userAreaDescription -> checkFileUploaded(userId, userAreaDescription))
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

    private Observable<UserAreaDescription> checkFileUploaded(String userId, UserAreaDescription userAreaDescription) {
        return userAreaDescriptionFileRepository
                .exists(userId, userAreaDescription.areaDescriptionId)
                .map(fileUploaded -> {
                    userAreaDescription.fileUploaded = fileUploaded;
                    return userAreaDescription;
                }).toObservable();
    }
}
