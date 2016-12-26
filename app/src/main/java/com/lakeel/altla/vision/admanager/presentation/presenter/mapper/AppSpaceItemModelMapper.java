package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

public final class AppSpaceItemModelMapper {

    @NonNull
    public AppSpaceItemModel map(@NonNull UserAreaDescription userAreaDescription) {
        AppSpaceItemModel model = new AppSpaceItemModel();
        model.id = userAreaDescription.areaDescriptionId;
        model.name = userAreaDescription.name;
        return model;
    }
}
