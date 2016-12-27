package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

public final class AppSpaceItemModelMapper {

    private AppSpaceItemModelMapper() {
    }

    @NonNull
    public static AppSpaceItemModel map(@NonNull UserAreaDescription userAreaDescription) {
        AppSpaceItemModel model = new AppSpaceItemModel();
        model.areaDescriptionId = userAreaDescription.areaDescriptionId;
        model.name = userAreaDescription.name;
        model.fileCached = userAreaDescription.fileCached;
        model.fileUploaded = userAreaDescription.fileUploaded;
        return model;
    }
}
