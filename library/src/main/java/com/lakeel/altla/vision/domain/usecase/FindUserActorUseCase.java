package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserActor;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public final class FindUserActorUseCase {

    @Inject
    UserActorRepository userActorRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserActorUseCase() {
    }

    @NonNull
    public Maybe<UserActor> execute(@NonNull String sceneId, @NonNull String actorId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.<UserActor>create(e -> {
            userActorRepository.find(userId, sceneId, actorId, userActor -> {
                if (userActor == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(userActor);
                }
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
