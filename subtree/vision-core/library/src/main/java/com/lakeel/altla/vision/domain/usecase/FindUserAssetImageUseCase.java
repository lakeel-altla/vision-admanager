package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserAssetImage;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public final class FindUserAssetImageUseCase {

    @Inject
    UserAssetImageRepository userAssetImageRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserAssetImageUseCase() {
    }

    @NonNull
    public Maybe<UserAssetImage> execute(@NonNull String assetId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.<UserAssetImage>create(e -> {
            userAssetImageRepository.find(userId, assetId, userActorImage -> {
                if (userActorImage == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(userActorImage);
                }
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
