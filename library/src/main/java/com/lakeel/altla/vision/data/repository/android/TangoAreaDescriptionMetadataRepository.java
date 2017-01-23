package com.lakeel.altla.vision.data.repository.android;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.vision.ArgumentNullException;

import java.util.ArrayList;
import java.util.List;

public final class TangoAreaDescriptionMetadataRepository {

    public TangoAreaDescriptionMetaData find(Tango tango, String areaDescriptionId) {
        if (tango == null) throw new ArgumentNullException("tango");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return tango.loadAreaDescriptionMetaData(areaDescriptionId);
    }

    public List<TangoAreaDescriptionMetaData> findAll(Tango tango) {
        if (tango == null) throw new ArgumentNullException("tango");

        List<String> areaDescriptionIds = tango.listAreaDescriptions();
        List<TangoAreaDescriptionMetaData> metaDatas = new ArrayList<>(areaDescriptionIds.size());

        for (String areaDescriptionId : areaDescriptionIds) {
            TangoAreaDescriptionMetaData metaData = find(tango, areaDescriptionId);
            metaDatas.add(metaData);
        }

        return metaDatas;
    }

    public void delete(Tango tango, String areaDescriptionId) {
        if (tango == null) throw new ArgumentNullException("tango");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        tango.deleteAreaDescription(areaDescriptionId);
    }
}
