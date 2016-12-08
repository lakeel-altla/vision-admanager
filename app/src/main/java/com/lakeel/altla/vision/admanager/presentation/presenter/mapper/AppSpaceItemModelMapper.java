package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.domain.model.AreaDescriptionEntry;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;

import android.support.annotation.NonNull;

public final class AppSpaceItemModelMapper {

    @NonNull
    public AppSpaceItemModel map(@NonNull AreaDescriptionEntry entry) {
        AppSpaceItemModel model = new AppSpaceItemModel();
        model.id = entry.id;
        model.name = entry.name;
        return model;
    }
}
