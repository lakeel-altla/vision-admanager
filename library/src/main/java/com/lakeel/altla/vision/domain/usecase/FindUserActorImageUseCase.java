package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorImageRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserActorImage;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public final class FindUserActorImageUseCase {

    @Inject
    UserActorImageRepository userActorImageRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserActorImageUseCase() {
    }

    @NonNull
    public Maybe<UserActorImage> execute(@NonNull String imageId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.<UserActorImage>create(e -> {
            userActorImageRepository.find(userId, imageId, userActorImage -> {
                if (userActorImage == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(userActorImage);
                }
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
