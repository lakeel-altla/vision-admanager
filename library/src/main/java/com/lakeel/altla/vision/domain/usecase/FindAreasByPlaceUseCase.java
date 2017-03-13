package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.PublicAreaRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.helper.AreaNameComparater;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Area;
import com.lakeel.altla.vision.domain.model.AreaScope;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class FindAreasByPlaceUseCase {

    @Inject
    PublicAreaRepository publicAreaRepository;

    @Inject
    UserAreaRepository userAreaRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindAreasByPlaceUseCase() {
    }

    @NonNull
    public Single<List<Area>> execute(@NonNull AreaScope areaScope, @NonNull String placeId) {
        if (areaScope == AreaScope.UNKNOWN) throw new IllegalArgumentException("Unknown area scope.");

        String userId = currentUserResolver.getUserId();

        return Single.<List<Area>>create(e -> {
            switch (areaScope) {
                case PUBLIC: {
                    publicAreaRepository.findByPlaceId(placeId, areas -> {
                        Collections.sort(areas, AreaNameComparater.INSTANCE);
                        e.onSuccess(areas);
                    }, e::onError);
                    break;
                }
                case USER: {
                    userAreaRepository.findByPlaceId(userId, placeId, areas -> {
                        Collections.sort(areas, AreaNameComparater.INSTANCE);
                        e.onSuccess(areas);
                    }, e::onError);
                    break;
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
