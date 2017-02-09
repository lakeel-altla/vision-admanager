package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionSceneRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserAreaDescriptionScene;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindAllUserAreaDescriptionsSceneUseCase {

    @Inject
    UserAreaDescriptionSceneRepository userAreaDescriptionSceneRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindAllUserAreaDescriptionsSceneUseCase() {
    }

    @NonNull
    public Observable<UserAreaDescriptionScene> execute(@NonNull String areaDescriptionId) {
        String userId = currentUserResolver.getUserId();

        return Observable.<UserAreaDescriptionScene>create(e -> {
            userAreaDescriptionSceneRepository.findAll(userId, areaDescriptionId, userAreaDescriptionScenes -> {
                for (UserAreaDescriptionScene userAreaDescriptionScene : userAreaDescriptionScenes) {
                    e.onNext(userAreaDescriptionScene);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
