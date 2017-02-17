package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserAreaDescriptionsByAreaIdUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveUserAreaDescriptionsByAreaIdUseCase() {
    }

    @NonNull
    public Observable<DataListEvent<UserAreaDescription>> execute(@NonNull String areaId) {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userAreaDescriptionRepository.observeByAreaId(userId, areaId))
                                 .subscribeOn(Schedulers.io());
    }
}
