package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.Actor;

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
    public Observable<DataListEvent<Actor>> execute(@NonNull String areaId) {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userActorRepository.observeAll(userId, areaId))
                                 .subscribeOn(Schedulers.io());
    }
}
