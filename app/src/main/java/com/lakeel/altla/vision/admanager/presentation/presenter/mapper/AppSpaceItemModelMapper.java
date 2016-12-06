package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;

import android.support.annotation.NonNull;

public final class AppSpaceItemModelMapper {

    @NonNull
    public AppSpaceItemModel map(@NonNull AreaDescriptionMetadata metadata) {
        AppSpaceItemModel itemModel = new AppSpaceItemModel();
        itemModel.uuid = metadata.uuid;
        itemModel.name = metadata.name;
        return itemModel;
    }
}
