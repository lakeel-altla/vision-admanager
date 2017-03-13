package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.PublicAreaDescriptionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.AreaDescription;
import com.lakeel.altla.vision.domain.model.AreaScope;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;

public final class FindAreaDescriptionUseCase {

    @Inject
    PublicAreaDescriptionRepository publicAreaDescriptionRepository;

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindAreaDescriptionUseCase() {
    }

    @NonNull
    public Maybe<AreaDescription> execute(@NonNull AreaScope areaScope, @NonNull String areaDescriptionId) {
        if (areaScope == AreaScope.UNKNOWN) throw new IllegalArgumentException("Unknown area scope.");

        String userId = currentUserResolver.getUserId();

        return Maybe.create(e -> {
            switch (areaScope) {
                case PUBLIC: {
                    publicAreaDescriptionRepository.find(areaDescriptionId, areaDescription -> {
                        if (areaDescription == null) {
                            e.onComplete();
                        } else {
                            e.onSuccess(areaDescription);
                        }
                    }, e::onError);
                    break;
                }
                case USER: {
                    userAreaDescriptionRepository.find(userId, areaDescriptionId, areaDescription -> {
                        if (areaDescription == null) {
                            e.onComplete();
                        } else {
                            e.onSuccess(areaDescription);
                        }
                    }, e::onError);
                    break;
                }
            }
        });
    }
}
