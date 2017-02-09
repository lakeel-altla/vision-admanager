package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import android.support.annotation.NonNull;

public final class PlaceModel {

    public final String placeId;

    public String name;

    public String address;

    public PlaceModel(@NonNull String placeId) {
        this.placeId = placeId;
    }
}
