package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaModel;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

public final class UserAreaModelMapper {

    private UserAreaModelMapper() {
    }

    @NonNull
    public static UserAreaModel map(@NonNull UserArea userArea) {
        UserAreaModel model = new UserAreaModel();
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
    public static UserArea map(@NonNull UserAreaModel model) {
        UserArea userArea = new UserArea(model.userId, model.areaId);
        userArea.name = model.name;
        userArea.placeId = model.placeId;
        userArea.level = model.level;
        userArea.createdAt = model.createdAt;
        userArea.updatedAt = model.updatedAt;
        return userArea;
    }
}
