package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.domain.model.AreaDescriptionEntry;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;

import android.support.annotation.NonNull;

public final class TangoSpaceItemModelMapper {

    @NonNull
    public TangoSpaceItemModel map(@NonNull AreaDescriptionEntry entry) {
        TangoSpaceItemModel itemModel = new TangoSpaceItemModel();
        itemModel.id = entry.id;
        itemModel.name = entry.name;
        return itemModel;
    }
}
