package com.lakeel.altla.vision.admanager.presentation.presenter.mapper;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.PlaceModel;

import android.support.annotation.NonNull;

public final class PlaceModelMapper {

    private PlaceModelMapper() {
    }

    @NonNull
    public static PlaceModel map(@NonNull Place place) {
        PlaceModel model = new PlaceModel();
        model.placeId = place.getId();
        model.name = place.getName().toString();
        model.address = place.getAddress().toString();
        return model;
    }
}
