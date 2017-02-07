package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.data.repository.firebase.UserProfileRepository;
import com.lakeel.altla.vision.domain.helper.ObservableDataObservable;
import com.lakeel.altla.vision.domain.model.UserProfile;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserProfileUseCase {

    @Inject
    UserProfileRepository userProfileRepository;

    @Inject
    public ObserveUserProfileUseCase() {
    }

    @NonNull
    public Observable<UserProfile> execute() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return ObservableDataObservable
                .using(() -> userProfileRepository.observe(user.getUid()))
                .subscribeOn(Schedulers.io());
    }
}
