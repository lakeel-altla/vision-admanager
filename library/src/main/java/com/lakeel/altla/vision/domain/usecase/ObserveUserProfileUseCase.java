package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserProfileRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.model.Profile;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserProfileUseCase {

    @Inject
    UserProfileRepository userProfileRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveUserProfileUseCase() {
    }

    @NonNull
    public Observable<Profile> execute() {
        String userId = currentUserResolver.getUserId();

        return ObservableData
                .using(() -> userProfileRepository.observe(userId))
                .subscribeOn(Schedulers.io());
    }
}
