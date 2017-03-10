package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.model.Actor;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserActorUseCase {

    @Inject
    UserActorRepository userActorRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveUserActorUseCase() {
    }

    @NonNull
    public Observable<Actor> execute(@NonNull String areaId, @NonNull String actorId) {
        String userId = currentUserResolver.getUserId();

        return ObservableData
                .using(() -> userActorRepository.observe(userId, areaId, actorId))
                .subscribeOn(Schedulers.io());
    }

}
