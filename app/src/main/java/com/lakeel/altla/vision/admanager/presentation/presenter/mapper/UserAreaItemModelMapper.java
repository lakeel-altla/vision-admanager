package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaItemModel;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

public final class UserAreaItemModelMapper {

    private UserAreaItemModelMapper() {
    }

    @NonNull
    public static UserAreaItemModel map(@NonNull UserArea userArea) {
        UserAreaItemModel model = new UserAreaItemModel(userArea.userId, userArea.areaId);
        model.name = userArea.name;
        model.placeId = userArea.placeId;
        model.level = String.valueOf(userArea.level);
        return model;
    }
}
