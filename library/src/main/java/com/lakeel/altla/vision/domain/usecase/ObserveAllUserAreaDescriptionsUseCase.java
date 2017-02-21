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

public final class ObserveAllUserAreaDescriptionsUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveAllUserAreaDescriptionsUseCase() {
    }

    @NonNull
    public Observable<DataListEvent<UserAreaDescription>> execute() {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userAreaDescriptionRepository.observeAll(userId))
                                 .subscribeOn(Schedulers.io());
    }
}
