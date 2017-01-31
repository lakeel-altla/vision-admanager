package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoAreaDescriptionItemModel;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;

import android.support.annotation.NonNull;

public final class TangoAreaDescriptionItemModelMapper {

    private TangoAreaDescriptionItemModelMapper() {
    }

    @NonNull
    public static TangoAreaDescriptionItemModel map(@NonNull TangoAreaDescription tangoAreaDescription) {
        TangoAreaDescriptionItemModel model = new TangoAreaDescriptionItemModel();
        model.areaDescriptionId = tangoAreaDescription.areaDescriptionId;
        model.name = tangoAreaDescription.name;
        return model;
    }
}
