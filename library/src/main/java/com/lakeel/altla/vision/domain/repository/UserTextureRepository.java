package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.UserTexture;

import rx.Completable;
import rx.Observable;

public interface UserTextureRepository {

    Completable save(UserTexture userTexture);

    Observable<UserTexture> find(String userId, String textureId);

    Observable<UserTexture> findAll(String userId);

    Completable delete(String userId, String textureId);
}
