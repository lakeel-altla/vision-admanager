package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Area;

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
    public Maybe<Area> execute(@NonNull String areaId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.create(e -> {
            userAreaRepository.find(userId, areaId, area -> {
                if (area == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(area);
                }
            }, e::onError);
        });
    }
}
