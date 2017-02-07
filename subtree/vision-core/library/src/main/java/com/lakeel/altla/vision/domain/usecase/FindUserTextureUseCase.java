package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.model.UserTexture;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public final class FindUserTextureUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    public FindUserTextureUseCase() {
    }

    @NonNull
    public Maybe<UserTexture> execute(@NonNull String textureId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return Maybe.<UserTexture>create(e -> userTextureRepository.find(user.getUid(), textureId, userTexture -> {
            if (userTexture != null) {
                e.onSuccess(userTexture);
            } else {
                e.onComplete();
            }
        }, e::onError)).subscribeOn(Schedulers.io());
    }
}
