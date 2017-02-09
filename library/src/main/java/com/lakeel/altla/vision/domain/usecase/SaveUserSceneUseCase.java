package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserSceneRepository;
import com.lakeel.altla.vision.domain.model.UserScene;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserSceneUseCase {

    @Inject
    UserSceneRepository userSceneRepository;

    @Inject
    public SaveUserSceneUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull UserScene userScene) {
        return Completable.create(e -> {
            userSceneRepository.save(userScene);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
