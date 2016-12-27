package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;

import android.support.annotation.NonNull;

public final class TangoSpaceItemModelMapper {

    private TangoSpaceItemModelMapper() {
    }

    @NonNull
    public static final TangoSpaceItemModel map(@NonNull TangoAreaDescription tangoAreaDescription) {
        TangoSpaceItemModel model = new TangoSpaceItemModel();
        model.areaDescriptionId = tangoAreaDescription.areaDescriptionId;
        model.name = tangoAreaDescription.name;
        return model;
    }
}
