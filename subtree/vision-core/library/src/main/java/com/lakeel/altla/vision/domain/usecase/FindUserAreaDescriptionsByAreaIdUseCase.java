package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.AreaDescription;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindUserAreaDescriptionsByAreaIdUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserAreaDescriptionsByAreaIdUseCase() {
    }

    @NonNull
    public Observable<AreaDescription> execute(@NonNull String areaId) {
        String userId = currentUserResolver.getUserId();

        return Observable.<AreaDescription>create(e -> {
            userAreaDescriptionRepository.findByAreaId(userId, areaId, areaDescriptions -> {
                for (AreaDescription areaDescription : areaDescriptions) {
                    e.onNext(areaDescription);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
