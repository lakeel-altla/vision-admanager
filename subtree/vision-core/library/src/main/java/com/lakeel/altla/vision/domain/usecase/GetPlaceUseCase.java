package com.lakeel.altla.vision.domain.usecase;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.data.repository.android.PlaceRepository;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class GetPlaceUseCase {

    @Inject
    PlaceRepository placeRepository;

    @Inject
    public GetPlaceUseCase() {
    }

    @NonNull
    public Single<Place> execute(@NonNull GoogleApiClient googleApiClient, @NonNull String placeId) {
        return Single.<Place>create(e -> {
            placeRepository.get(googleApiClient, placeId, e::onSuccess, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
