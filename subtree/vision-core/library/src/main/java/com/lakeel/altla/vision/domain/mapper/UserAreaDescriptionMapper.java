package com.lakeel.altla.vision.domain.mapper;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

public final class UserAreaDescriptionMapper {

    private UserAreaDescriptionMapper() {
    }

    public static UserAreaDescription map(TangoAreaDescriptionMetaData metaData) {
        String areaDescriptionId = TangoAreaDescriptionMetaDataHelper.getUuid(metaData);
        String name = TangoAreaDescriptionMetaDataHelper.getName(metaData);
        long creationTime = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(metaData);

        return new UserAreaDescription(areaDescriptionId, name, creationTime);
    }
}
