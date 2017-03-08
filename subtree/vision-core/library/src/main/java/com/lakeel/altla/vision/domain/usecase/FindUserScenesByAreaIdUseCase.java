package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserSceneRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Scene;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindUserScenesByAreaIdUseCase {

    @Inject
    UserSceneRepository userSceneRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserScenesByAreaIdUseCase() {
    }

    @NonNull
    public Observable<Scene> execute(@NonNull String areaId) {
        String userId = currentUserResolver.getUserId();

        return Observable.<Scene>create(e -> {
            userSceneRepository.findByAreaId(userId, areaId, scenes -> {
                for (Scene scene : scenes) {
                    e.onNext(scene);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
