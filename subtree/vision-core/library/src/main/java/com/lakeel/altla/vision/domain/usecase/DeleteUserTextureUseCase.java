package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.android.TextureCacheRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DeleteUserTextureUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    UserTextureFileRepository userTextureFileRepository;

    @Inject
    TextureCacheRepository textureCacheRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public DeleteUserTextureUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull String textureId) {
        String userId = currentUserResolver.getUserId();

        return Completable.create(e -> {
            // Delete the user texture in Firebase Database.
            userTextureRepository.delete(userId, textureId);
            // Delete the user texture file in Firebase Storage.
            userTextureFileRepository.delete(userId, textureId, aVoid -> {
                // Delete the local cache of the user texture.
                textureCacheRepository.delete(textureId);
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
