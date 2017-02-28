package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.model.UserAssetImage;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveUserAssetImageUseCase {

    @Inject
    UserAssetImageRepository userAssetImageRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveUserAssetImageUseCase() {
    }

    @NonNull
    public Observable<UserAssetImage> execute(@NonNull String assetId) {
        String userId = currentUserResolver.getUserId();

        return ObservableData
                .using(() -> userAssetImageRepository.observe(userId, assetId))
                .subscribeOn(Schedulers.io());
    }
}
