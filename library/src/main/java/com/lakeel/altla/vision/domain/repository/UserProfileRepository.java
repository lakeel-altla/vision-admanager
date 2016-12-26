package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.UserProfile;

import rx.Completable;
import rx.Observable;

public interface UserProfileRepository {

    Completable save(UserProfile userProfile);

    Observable<UserProfile> find(String userId);

    Observable<UserProfile> observe(String userId);
}
