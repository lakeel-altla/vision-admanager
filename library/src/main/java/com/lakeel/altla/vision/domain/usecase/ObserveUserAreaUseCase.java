package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserAreaUseCase {

    @Inject
    UserAreaRepository userAreaRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveUserAreaUseCase() {
    }

    @NonNull
    public Observable<UserArea> execute(@NonNull String areaId) {
        String userId = currentUserResolver.getUserId();

        return ObservableData
                .using(() -> userAreaRepository.observe(userId, areaId))
                .subscribeOn(Schedulers.io());
    }
}
