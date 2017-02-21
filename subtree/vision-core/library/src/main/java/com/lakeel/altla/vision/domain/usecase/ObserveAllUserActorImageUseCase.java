package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorImageRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.UserActorImage;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveAllUserActorImageUseCase {

    @Inject
    UserActorImageRepository userActorImageRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveAllUserActorImageUseCase() {
    }

    @NonNull
    public Observable<DataListEvent<UserActorImage>> execute() {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userActorImageRepository.observeAll(userId))
                                 .subscribeOn(Schedulers.io());
    }
}
