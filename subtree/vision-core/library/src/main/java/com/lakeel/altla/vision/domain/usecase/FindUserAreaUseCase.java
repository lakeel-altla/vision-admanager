package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.model.UserArea;

import javax.inject.Inject;

import io.reactivex.Maybe;

public final class FindUserAreaUseCase {

    @Inject
    UserAreaRepository userAreaRepository;

    @Inject
    public FindUserAreaUseCase() {
    }

    public Maybe<UserArea> execute(String areaId) {
        if (areaId == null) throw new ArgumentNullException("areaId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return Maybe.create(e -> {
            userAreaRepository.find(user.getUid(), areaId, userAreaDescription -> {
                if (userAreaDescription != null) {
                    e.onSuccess(userAreaDescription);
                } else {
                    e.onComplete();
                }
            }, e::onError);
        });
    }
}
