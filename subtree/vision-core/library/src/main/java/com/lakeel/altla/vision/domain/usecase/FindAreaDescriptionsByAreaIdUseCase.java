package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.PublicAreaDescriptionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.AreaDescriptionNameComparater;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.AreaDescription;
import com.lakeel.altla.vision.domain.model.AreaScope;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class FindAreaDescriptionsByAreaIdUseCase {

    @Inject
    PublicAreaDescriptionRepository publicAreaDescriptionRepository;

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindAreaDescriptionsByAreaIdUseCase() {
    }

    @NonNull
    public Single<List<AreaDescription>> execute(@NonNull AreaScope areaScope, @NonNull String areaId) {
        if (areaScope == AreaScope.UNKNOWN) throw new IllegalArgumentException("Unknown area scope.");

        String userId = currentUserResolver.getUserId();

        return Single.<List<AreaDescription>>create(e -> {
            switch (areaScope) {
                case PUBLIC: {
                    publicAreaDescriptionRepository.findByAreaId(areaId, areaDescriptions -> {
                        Collections.sort(areaDescriptions, AreaDescriptionNameComparater.INSTANCE);
                        e.onSuccess(areaDescriptions);
                    }, e::onError);
                    break;
                }
                case USER: {
                    userAreaDescriptionRepository.findByAreaId(userId, areaId, areaDescriptions -> {
                        Collections.sort(areaDescriptions, AreaDescriptionNameComparater.INSTANCE);
                        e.onSuccess(areaDescriptions);
                    }, e::onError);
                    break;
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
