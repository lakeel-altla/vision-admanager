package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindAllUserAreasUseCase {

    @Inject
    UserAreaRepository userAreaRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindAllUserAreasUseCase() {
    }

    @NonNull
    public Observable<UserArea> execute() {
        String userId = currentUserResolver.getUserId();

        return Observable.<UserArea>create(e -> {
            userAreaRepository.findAll(userId, userAreas -> {
                for (UserArea userArea : userAreas) {
                    e.onNext(userArea);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
