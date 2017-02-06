package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoAreaDescriptionModel;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;

import android.support.annotation.NonNull;

public final class TangoAreaDescriptionModelMapper {

    private TangoAreaDescriptionModelMapper() {
    }

    @NonNull
    public static TangoAreaDescriptionModel map(@NonNull TangoAreaDescription tangoAreaDescription) {
        TangoAreaDescriptionModel model = new TangoAreaDescriptionModel();
        model.areaDescriptionId = tangoAreaDescription.areaDescriptionId;
        model.name = tangoAreaDescription.name;
        model.createdAt = tangoAreaDescription.createdAt;
        return model;
    }
}
