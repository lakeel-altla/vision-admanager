package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.PublicAreaRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Area;
import com.lakeel.altla.vision.domain.model.AreaScope;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;

public final class FindAreaUseCase {

    @Inject
    PublicAreaRepository publicAreaRepository;

    @Inject
    UserAreaRepository userAreaRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindAreaUseCase() {
    }

    @NonNull
    public Maybe<Area> execute(@NonNull AreaScope areaScope, @NonNull String areaId) {
        if (areaScope == AreaScope.UNKNOWN) throw new IllegalArgumentException("Unknown area scope.");

        String userId = currentUserResolver.getUserId();

        return Maybe.create(e -> {
            switch (areaScope) {
                case PUBLIC: {
                    publicAreaRepository.find(areaId, area -> {
                        if (area == null) {
                            e.onComplete();
                        } else {
                            e.onSuccess(area);
                        }
                    }, e::onError);
                    break;
                }
                case USER: {
                    userAreaRepository.find(userId, areaId, area -> {
                        if (area == null) {
                            e.onComplete();
                        } else {
                            e.onSuccess(area);
                        }
                    }, e::onError);
                    break;
                }
            }
        });
    }
}
