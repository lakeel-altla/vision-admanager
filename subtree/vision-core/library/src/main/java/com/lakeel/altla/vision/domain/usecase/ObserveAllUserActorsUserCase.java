package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.UserActor;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveAllUserActorsUserCase {

    @Inject
    UserActorRepository userActorRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveAllUserActorsUserCase() {
    }

    @NonNull
    public Observable<DataListEvent<UserActor>> execute(@NonNull String sceneId) {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userActorRepository.observeAll(userId, sceneId))
                                 .subscribeOn(Schedulers.io());
    }
}
