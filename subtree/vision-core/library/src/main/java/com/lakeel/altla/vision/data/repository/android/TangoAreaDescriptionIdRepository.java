package com.lakeel.altla.vision.data.repository.android;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.vision.ArgumentNullException;

import java.util.List;

public final class TangoAreaDescriptionIdRepository {

    public List<String> findAll(Tango tango) {
        if (tango == null) throw new ArgumentNullException("tango");

        return tango.listAreaDescriptions();
    }
}
