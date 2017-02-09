package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserSceneRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserScene;

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
    public Maybe<UserScene> execute(@NonNull String sceneId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.<UserScene>create(e -> {
            userSceneRepository.find(userId, sceneId, userScene -> {
                if (userScene != null) {
                    e.onSuccess(userScene);
                } else {
                    e.onComplete();
                }
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
