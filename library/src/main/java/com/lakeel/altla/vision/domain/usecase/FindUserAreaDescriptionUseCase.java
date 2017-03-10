package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.AreaDescription;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;

public final class FindUserAreaDescriptionUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserAreaDescriptionUseCase() {
    }

    @NonNull
    public Maybe<AreaDescription> execute(@NonNull String areaDescriptionId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.create(e -> {
            userAreaDescriptionRepository.find(userId, areaDescriptionId, areaDescription -> {
                if (areaDescription == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(areaDescription);
                }
            }, e::onError);
        });
    }
}
