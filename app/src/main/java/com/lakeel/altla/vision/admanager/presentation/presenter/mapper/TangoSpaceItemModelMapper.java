package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;

import android.support.annotation.NonNull;

public final class TangoSpaceItemModelMapper {

    @NonNull
    public TangoSpaceItemModel map(@NonNull AreaDescriptionMetadata metadata) {
        TangoSpaceItemModel itemModel = new TangoSpaceItemModel();
        itemModel.uuid = metadata.uuid;
        itemModel.name = metadata.name;
        return itemModel;
    }
}
