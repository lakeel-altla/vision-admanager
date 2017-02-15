package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaEditModel;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

public final class UserAreaEditModelMapper {

    private UserAreaEditModelMapper() {
    }

    @NonNull
    public static UserAreaEditModel map(@NonNull UserArea userArea) {
        UserAreaEditModel model = new UserAreaEditModel();
        model.userId = userArea.userId;
        model.areaId = userArea.areaId;
        model.name = userArea.name;
        model.placeId = userArea.placeId;
        model.level = userArea.level;
        model.createdAt = userArea.createdAt;
        model.updatedAt = userArea.updatedAt;
        return model;
    }

    @NonNull
    public static UserArea map(@NonNull UserAreaEditModel model) {
        UserArea userArea = new UserArea(model.userId, model.areaId);
        userArea.name = model.name;
        userArea.placeId = model.placeId;
        userArea.level = model.level;
        userArea.createdAt = model.createdAt;
        userArea.updatedAt = model.updatedAt;
        return userArea;
    }
}
