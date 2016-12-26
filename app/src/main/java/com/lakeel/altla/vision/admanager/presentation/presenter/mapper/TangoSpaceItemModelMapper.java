package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

public final class TangoSpaceItemModelMapper {

    @NonNull
    public TangoSpaceItemModel map(@NonNull UserAreaDescription userAreaDescription) {
        TangoSpaceItemModel itemModel = new TangoSpaceItemModel();
        itemModel.id = userAreaDescription.areaDescriptionId;
        itemModel.name = userAreaDescription.name;
        return itemModel;
    }
}
