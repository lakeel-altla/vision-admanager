package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionEditModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

public final class UserAreaDescriptionEditModelMapper {

    private UserAreaDescriptionEditModelMapper() {
    }

    @NonNull
    public static UserAreaDescriptionEditModel map(@NonNull UserAreaDescription userAreaDescription) {
        UserAreaDescriptionEditModel model = new UserAreaDescriptionEditModel();
        model.userId = userAreaDescription.userId;
        model.areaDescriptionId = userAreaDescription.areaDescriptionId;
        model.name = userAreaDescription.name;
        model.createdAt = userAreaDescription.createdAt;
        model.fileUploaded = userAreaDescription.fileUploaded;
        model.areaId = userAreaDescription.areaId;
        return model;
    }

    @NonNull
    public static UserAreaDescription map(@NonNull UserAreaDescriptionEditModel model) {
        UserAreaDescription userAreaDescription = new UserAreaDescription();
        userAreaDescription.userId = model.userId;
        userAreaDescription.areaDescriptionId = model.areaDescriptionId;
        userAreaDescription.name = model.name;
        userAreaDescription.createdAt = model.createdAt;
        userAreaDescription.fileUploaded = model.fileUploaded;
        userAreaDescription.areaId = model.areaId;
        return userAreaDescription;
    }
}
