package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;

import android.support.annotation.NonNull;

public final class AppSpaceItemModelMapper {

    @NonNull
    public AppSpaceItemModel map(@NonNull AreaDescriptionMetaData metaData) {
        AppSpaceItemModel itemModel = new AppSpaceItemModel();
        itemModel.uuid = metaData.uuid;
        itemModel.name = metaData.name;
        return itemModel;
    }
}
