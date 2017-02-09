package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;

public final class FindUserAreaUseCase {

    @Inject
    UserAreaRepository userAreaRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserAreaUseCase() {
    }

    @NonNull
    public Maybe<UserArea> execute(@NonNull String areaId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.create(e -> {
            userAreaRepository.find(userId, areaId, userAreaDescription -> {
                if (userAreaDescription != null) {
                    e.onSuccess(userAreaDescription);
                } else {
                    e.onComplete();
                }
            }, e::onError);
        });
    }
}
