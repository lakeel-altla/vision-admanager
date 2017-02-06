package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionItemModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

public final class UserAreaDescriptionItemModelMapper {

    private UserAreaDescriptionItemModelMapper() {
    }

    @NonNull
    public static UserAreaDescriptionItemModel map(@NonNull UserAreaDescription userAreaDescription) {
        UserAreaDescriptionItemModel model = new UserAreaDescriptionItemModel();
        model.areaDescriptionId = userAreaDescription.areaDescriptionId;
        model.name = userAreaDescription.name;
        return model;
    }
}
