package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.Area;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveAllUserAreasUseCase {

    @Inject
    UserAreaRepository userAreaRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveAllUserAreasUseCase() {
    }

    @NonNull
    public Observable<DataListEvent<Area>> execute() {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userAreaRepository.observeAll(userId))
                                 .subscribeOn(Schedulers.io());
    }
}
