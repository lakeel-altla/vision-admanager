package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserTexture;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public final class FindUserTextureUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindUserTextureUseCase() {
    }

    @NonNull
    public Maybe<UserTexture> execute(@NonNull String textureId) {
        String userId = currentUserResolver.getUserId();

        return Maybe.<UserTexture>create(e -> userTextureRepository.find(userId, textureId, userTexture -> {
            if (userTexture != null) {
                e.onSuccess(userTexture);
            } else {
                e.onComplete();
            }
        }, e::onError)).subscribeOn(Schedulers.io());
    }
}
