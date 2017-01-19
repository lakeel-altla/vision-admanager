package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.model.UserTexture;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllUserTexturesUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    public FindAllUserTexturesUseCase() {
    }

    public Observable<UserTexture> execute() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return Observable.<UserTexture>create(subscriber -> {
            userTextureRepository.findAll(user.getUid(), userTextures -> {
                for (UserTexture userTexture : userTextures) {
                    subscriber.onNext(userTexture);
                }
                subscriber.onCompleted();
            }, subscriber::onError);
        }).subscribeOn(Schedulers.io());
    }
}
