package com.lakeel.altla.vision.domain.mapper;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;

public final class TangoAreaDescriptionMapper {

    private TangoAreaDescriptionMapper() {
    }

    public static TangoAreaDescription map(TangoAreaDescriptionMetaData metaData) {
        if (metaData == null) throw new ArgumentNullException("metaData");

        TangoAreaDescription description = new TangoAreaDescription();
        description.areaDescriptionId = TangoAreaDescriptionMetaDataHelper.getUuid(metaData);
        description.name = TangoAreaDescriptionMetaDataHelper.getName(metaData);
        description.createdAt = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(metaData);
        return description;
    }
}
