package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.model.ImageAsset;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserImageAssetUseCase {

    @Inject
    UserImageAssetRepository userImageAssetRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveUserImageAssetUseCase() {
    }

    @NonNull
    public Observable<ImageAsset> execute(@NonNull String assetId) {
        String userId = currentUserResolver.getUserId();

        return ObservableData
                .using(() -> userImageAssetRepository.observe(userId, assetId))
                .subscribeOn(Schedulers.io());
    }
}
