package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserSceneRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.Scene;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserScenesByAreaIdUseCase {

    @Inject
    UserSceneRepository userSceneRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveUserScenesByAreaIdUseCase() {
    }

    @NonNull
    public Observable<DataListEvent<Scene>> execute(@NonNull String areaId) {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userSceneRepository.observeByAreaId(userId, areaId))
                                 .subscribeOn(Schedulers.io());
    }
}
