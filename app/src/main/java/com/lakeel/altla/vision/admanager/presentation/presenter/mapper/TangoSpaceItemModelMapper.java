package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;

import android.support.annotation.NonNull;

public final class TangoSpaceItemModelMapper {

    @NonNull
    public TangoSpaceItemModel map(@NonNull AreaDescriptionMetaData metaData) {
        TangoSpaceItemModel itemModel = new TangoSpaceItemModel();
        itemModel.uuid = metaData.uuid;
        itemModel.name = metaData.name;
        return itemModel;
    }
}
