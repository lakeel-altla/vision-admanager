package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.model.UserTexture;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindAllUserTexturesUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    public FindAllUserTexturesUseCase() {
    }

    @NonNull
    public Observable<UserTexture> execute() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return Observable.<UserTexture>create(e -> {
            userTextureRepository.findAll(user.getUid(), userTextures -> {
                for (UserTexture userTexture : userTextures) {
                    e.onNext(userTexture);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
