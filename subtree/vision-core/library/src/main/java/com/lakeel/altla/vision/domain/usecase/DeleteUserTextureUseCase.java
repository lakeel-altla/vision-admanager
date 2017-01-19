package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.TextureCacheRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;

import javax.inject.Inject;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.schedulers.Schedulers;

public final class DeleteUserTextureUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    UserTextureFileRepository userTextureFileRepository;

    @Inject
    TextureCacheRepository textureCacheRepository;

    @Inject
    public DeleteUserTextureUseCase() {
    }

    public Completable execute(String textureId) {
        if (textureId == null) throw new ArgumentNullException("textureId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        String userId = user.getUid();

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                // Delete the user texture in Firebase Database.
                userTextureRepository.delete(userId, textureId);
                // Delete the user texture file in Firebase Storage.
                userTextureFileRepository.delete(userId, textureId, aVoid -> {
                    // Delete the local cache of the user texture.
                    textureCacheRepository.delete(textureId);
                    subscriber.onCompleted();
                }, subscriber::onError);
            }
        }).subscribeOn(Schedulers.io());
    }
}
