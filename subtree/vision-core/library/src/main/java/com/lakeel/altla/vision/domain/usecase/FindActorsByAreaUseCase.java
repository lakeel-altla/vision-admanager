package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.PublicActorRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserActorRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Actor;
import com.lakeel.altla.vision.domain.model.AreaScope;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class FindActorsByAreaUseCase {

    @Inject
    PublicActorRepository publicActorRepository;

    @Inject
    UserActorRepository userActorRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindActorsByAreaUseCase() {
    }

    @NonNull
    public Single<List<Actor>> execute(@NonNull AreaScope areaScope, @NonNull String areaId) {
        if (areaScope == AreaScope.UNKNOWN) throw new IllegalArgumentException("Unknown area scope.");

        String userId = currentUserResolver.getUserId();

        return Single.<List<Actor>>create(e -> {
            switch (areaScope) {
                case PUBLIC: {
                    publicActorRepository.findByAreaId(areaId, actors -> {
                        // TODO: sort by something.
                        e.onSuccess(actors);
                    }, e::onError);
                    break;
                }
                case USER: {
                    userActorRepository.findByAreaId(userId, areaId, actors -> {
                        // TODO: sort by something.
                        e.onSuccess(actors);
                    }, e::onError);
                    break;
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
