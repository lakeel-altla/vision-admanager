package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.TextureCacheRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureFileRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import javax.inject.Inject;

import rx.Completable;
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

        return userTextureRepository
                // Delete the user texture in Firebase Database.
                .delete(userId, textureId)
                // Delete the user texture file in Firebase Storage.
                .andThen(userTextureFileRepository.delete(userId, textureId))
                // Delete the local cache of the user texture.
                .andThen(textureCacheRepository.delete(textureId))
                .subscribeOn(Schedulers.io());
    }
}
