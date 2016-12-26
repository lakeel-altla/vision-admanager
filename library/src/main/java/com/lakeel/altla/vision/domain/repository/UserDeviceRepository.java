package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.model.UserDevice;

import rx.Completable;

public interface UserDeviceRepository {

    Completable save(UserDevice userDevice);
}
