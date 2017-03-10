package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.ImageAsset;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public final class FindUserImageAssetUseCase {

    @Inject
    UserImageAssetRepository userImageAssetRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserImageAssetUseCase() {
    }

    @NonNull
    public Maybe<ImageAsset> execute(@NonNull String assetId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.<ImageAsset>create(e -> {
            userImageAssetRepository.find(userId, assetId, userActorImage -> {
                if (userActorImage == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(userActorImage);
                }
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
