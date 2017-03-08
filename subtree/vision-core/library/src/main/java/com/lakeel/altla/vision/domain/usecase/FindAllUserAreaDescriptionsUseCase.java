package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.AreaDescription;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindAllUserAreaDescriptionsUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindAllUserAreaDescriptionsUseCase() {
    }

    @NonNull
    public Observable<AreaDescription> execute() {
        String userId = currentUserResolver.getUserId();

        return Observable.<AreaDescription>create(e -> {
            userAreaDescriptionRepository.findAll(userId, areaDescriptions -> {
                for (AreaDescription areaDescription : areaDescriptions) {
                    e.onNext(areaDescription);
                }

                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
