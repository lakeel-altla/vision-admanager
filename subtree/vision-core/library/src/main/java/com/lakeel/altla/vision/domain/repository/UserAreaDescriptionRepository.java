package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import rx.Completable;
import rx.Observable;

public interface UserAreaDescriptionRepository {

    Completable save(String userId, UserAreaDescription userAreaDescription);

    Observable<UserAreaDescription> find(String userId, String areaDescriptionId);

    Observable<UserAreaDescription> findAll(String userId);

    Completable delete(String userId, String areaDescriptionId);
}
