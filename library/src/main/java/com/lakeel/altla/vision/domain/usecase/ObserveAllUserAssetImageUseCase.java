package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.UserAssetImage;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveAllUserAssetImageUseCase {

    @Inject
    UserAssetImageRepository userAssetImageRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveAllUserAssetImageUseCase() {
    }

    @NonNull
    public Observable<DataListEvent<UserAssetImage>> execute() {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userAssetImageRepository.observeAll(userId))
                                 .subscribeOn(Schedulers.io());
    }
}
