package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.domain.model.UserProfile;
import com.lakeel.altla.vision.domain.repository.UserProfileRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class ObserveUserProfileUseCase {

    @Inject
    UserProfileRepository userProfileRepository;

    @Inject
    public ObserveUserProfileUseCase() {
    }

    public Observable<UserProfile> execute() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return userProfileRepository.observe(user.getUid())
                                    .subscribeOn(Schedulers.io());
    }
}
