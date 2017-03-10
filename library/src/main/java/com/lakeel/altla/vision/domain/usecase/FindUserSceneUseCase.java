package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserSceneRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Scene;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public final class FindUserSceneUseCase {

    @Inject
    UserSceneRepository userSceneRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserSceneUseCase() {
    }

    @NonNull
    public Maybe<Scene> execute(@NonNull String sceneId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.<Scene>create(e -> {
            userSceneRepository.find(userId, sceneId, scene -> {
                if (scene == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(scene);
                }
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
