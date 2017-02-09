package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

public final class UserAreaDescriptionModelMapper {

    private UserAreaDescriptionModelMapper() {
    }

    @NonNull
    public static UserAreaDescriptionModel map(@NonNull UserAreaDescription userAreaDescription) {
        UserAreaDescriptionModel model = new UserAreaDescriptionModel(
                userAreaDescription.userId, userAreaDescription.areaDescriptionId);
        model.name = userAreaDescription.name;
        model.fileUploaded = userAreaDescription.fileUploaded;
        model.areaId = userAreaDescription.areaId;
        model.createdAt = userAreaDescription.createdAt;
        model.updatedAt = userAreaDescription.updatedAt;
        return model;
    }
}
