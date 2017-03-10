package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserSceneRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.model.Scene;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserSceneUseCase {

    @Inject
    UserSceneRepository userSceneRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveUserSceneUseCase() {
    }

    @NonNull
    public Observable<Scene> execute(@NonNull String sceneId) {
        String userId = currentUserResolver.getUserId();

        return ObservableData
                .using(() -> userSceneRepository.observe(userId, sceneId))
                .subscribeOn(Schedulers.io());
    }
}
