package com.lakeel.altla.vision.domain.mapper;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;

import android.support.annotation.NonNull;

public final class TangoAreaDescriptionMapper {

    private TangoAreaDescriptionMapper() {
    }

    @NonNull
    public static TangoAreaDescription map(@NonNull TangoAreaDescriptionMetaData metaData) {
        TangoAreaDescription description = new TangoAreaDescription(
                TangoAreaDescriptionMetaDataHelper.getUuid(metaData));
        description.name = TangoAreaDescriptionMetaDataHelper.getName(metaData);
        description.createdAt = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(metaData);
        return description;
    }
}
