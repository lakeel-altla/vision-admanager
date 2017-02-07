package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserTexture;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindAllUserTexturesUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public FindAllUserTexturesUseCase() {
    }

    @NonNull
    public Observable<UserTexture> execute() {
        String userId = currentUserResolver.getUserId();

        return Observable.<UserTexture>create(e -> {
            userTextureRepository.findAll(userId, userTextures -> {
                for (UserTexture userTexture : userTextures) {
                    e.onNext(userTexture);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
