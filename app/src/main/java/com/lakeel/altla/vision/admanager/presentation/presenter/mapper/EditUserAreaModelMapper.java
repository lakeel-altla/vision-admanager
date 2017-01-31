package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaModel;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

public final class EditUserAreaModelMapper {

    private EditUserAreaModelMapper() {
    }

    @NonNull
    public static EditUserAreaModel map(@NonNull UserArea userArea) {
        EditUserAreaModel model = new EditUserAreaModel();
        model.areaId = userArea.areaId;
        model.name = userArea.name;
        model.placeId = userArea.placeId;
        model.level = userArea.level;
        return model;
    }
}
