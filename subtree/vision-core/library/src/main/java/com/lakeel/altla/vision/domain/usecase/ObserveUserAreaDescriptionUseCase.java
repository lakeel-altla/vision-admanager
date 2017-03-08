package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.model.AreaDescription;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserAreaDescriptionUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveUserAreaDescriptionUseCase() {
    }

    @NonNull
    public Observable<AreaDescription> execute(@NonNull String areaDescriptionId) {
        String userId = currentUserResolver.getUserId();

        return ObservableData
                .using(() -> userAreaDescriptionRepository.observe(userId, areaDescriptionId))
                .subscribeOn(Schedulers.io());
    }
}
