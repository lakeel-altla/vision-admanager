package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Actor;

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
    public Maybe<Actor> execute(@NonNull String actorId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.<Actor>create(e -> {
            userActorRepository.find(userId, actorId, actor -> {
                if (actor == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(actor);
                }
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
