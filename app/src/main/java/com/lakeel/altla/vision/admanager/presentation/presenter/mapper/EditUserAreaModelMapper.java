package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaModel;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

public final class EditUserAreaModelMapper {

    private EditUserAreaModelMapper() {
    }

    @NonNull
    public static UserAreaModel map(@NonNull UserArea userArea) {
        UserAreaModel model = new UserAreaModel();
        model.areaId = userArea.areaId;
        model.name = userArea.name;
        model.createdAt = userArea.createdAt;
        model.placeId = userArea.placeId;
        model.level = userArea.level;
        return model;
    }
}
