package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

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
    public Observable<UserAreaDescription> execute(@NonNull String areaId) {
        String userId = currentUserResolver.getUserId();

        return Observable.<UserAreaDescription>create(e -> {
            userAreaDescriptionRepository.findByAreaId(userId, areaId, userAreaDescriptions -> {
                for (UserAreaDescription userAreaDescription : userAreaDescriptions) {
                    e.onNext(userAreaDescription);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
