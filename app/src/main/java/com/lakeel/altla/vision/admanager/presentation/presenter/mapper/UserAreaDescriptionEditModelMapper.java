package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionEditModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

public final class UserAreaDescriptionEditModelMapper {

    private UserAreaDescriptionEditModelMapper() {
    }

    @NonNull
    public static UserAreaDescriptionEditModel map(@NonNull UserAreaDescription userAreaDescription) {
        UserAreaDescriptionEditModel model = new UserAreaDescriptionEditModel(
                userAreaDescription.userId, userAreaDescription.areaDescriptionId);
        model.name = userAreaDescription.name;
        model.fileUploaded = userAreaDescription.fileUploaded;
        model.areaId = userAreaDescription.areaId;
        model.createdAt = userAreaDescription.createdAt;
        model.updatedAt = userAreaDescription.updatedAt;
        return model;
    }

    @NonNull
    public static UserAreaDescription map(@NonNull UserAreaDescriptionEditModel model) {
        UserAreaDescription userAreaDescription = new UserAreaDescription(model.userId, model.areaDescriptionId);
        userAreaDescription.name = model.name;
        userAreaDescription.fileUploaded = model.fileUploaded;
        userAreaDescription.areaId = model.areaId;
        userAreaDescription.createdAt = model.createdAt;
        userAreaDescription.updatedAt = model.updatedAt;
        return userAreaDescription;
    }
}
