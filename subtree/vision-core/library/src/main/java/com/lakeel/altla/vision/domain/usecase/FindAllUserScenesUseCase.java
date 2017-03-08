package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserSceneRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Scene;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindAllUserScenesUseCase {

    @Inject
    UserSceneRepository userSceneRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindAllUserScenesUseCase() {
    }

    @NonNull
    public Observable<Scene> execute() {
        String userId = currentUserResolver.getUserId();

        return Observable.<Scene>create(e -> {
            userSceneRepository.findAll(userId, scenes -> {
                for (Scene scene : scenes) {
                    e.onNext(scene);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
