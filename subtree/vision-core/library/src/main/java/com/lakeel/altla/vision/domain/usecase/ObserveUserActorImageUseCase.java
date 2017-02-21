package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorImageRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.model.UserActorImage;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserActorImageUseCase {

    @Inject
    UserActorImageRepository userActorImageRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveUserActorImageUseCase() {
    }

    @NonNull
    public Observable<UserActorImage> execute(@NonNull String areaId) {
        String userId = currentUserResolver.getUserId();

        return ObservableData
                .using(() -> userActorImageRepository.observe(userId, areaId))
                .subscribeOn(Schedulers.io());
    }
}
