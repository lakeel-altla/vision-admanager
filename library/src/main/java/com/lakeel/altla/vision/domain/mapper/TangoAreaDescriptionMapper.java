package com.lakeel.altla.vision.domain.mapper;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;

public final class TangoAreaDescriptionMapper {

    private TangoAreaDescriptionMapper() {
    }

    public static final TangoAreaDescription map(TangoAreaDescriptionMetaData metaData) {
        String areaDescriptionId = TangoAreaDescriptionMetaDataHelper.getUuid(metaData);
        String name = TangoAreaDescriptionMetaDataHelper.getName(metaData);
        long creationTime = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(metaData);

        return new TangoAreaDescription(areaDescriptionId, name, creationTime);
    }
}
