package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import javax.inject.Inject;

import io.reactivex.Maybe;

public final class FindUserAreaDescriptionUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    public FindUserAreaDescriptionUseCase() {
    }

    public Maybe<UserAreaDescription> execute(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return Maybe.create(e -> {
            userAreaDescriptionRepository.find(user.getUid(), areaDescriptionId, userAreaDescription -> {
                if (userAreaDescription != null) {
                    e.onSuccess(userAreaDescription);
                } else {
                    e.onComplete();
                }
            }, e::onError);
        });
    }
}
