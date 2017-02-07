package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindUserAreaDescriptionsByAreaIdUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    public FindUserAreaDescriptionsByAreaIdUseCase() {
    }

    @NonNull
    public Observable<UserAreaDescription> execute(@NonNull String areaId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return Observable.<UserAreaDescription>create(e -> {
            userAreaDescriptionRepository.findByAreaId(user.getUid(), areaId, userAreaDescriptions -> {
                for (UserAreaDescription userAreaDescription : userAreaDescriptions) {
                    e.onNext(userAreaDescription);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
