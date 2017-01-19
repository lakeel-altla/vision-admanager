package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.model.UserTexture;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindUserTextureUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    public FindUserTextureUseCase() {
    }

    public Observable<UserTexture> execute(String textureId) {
        if (textureId == null) throw new ArgumentNullException("textureId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return userTextureRepository.find(user.getUid(), textureId)
                                    .subscribeOn(Schedulers.io());
    }
}
