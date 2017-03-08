package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.ImageAsset;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveAllUserImageAssetUseCase {

    @Inject
    UserImageAssetRepository userImageAssetRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveAllUserImageAssetUseCase() {
    }

    @NonNull
    public Observable<DataListEvent<ImageAsset>> execute() {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userImageAssetRepository.observeAll(userId))
                                 .subscribeOn(Schedulers.io());
    }
}
