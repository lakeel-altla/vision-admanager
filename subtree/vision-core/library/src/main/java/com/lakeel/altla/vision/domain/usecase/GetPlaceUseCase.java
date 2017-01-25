package com.lakeel.altla.vision.domain.usecase;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.PlaceRepository;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class GetPlaceUseCase {

    @Inject
    PlaceRepository placeRepository;

    @Inject
    public GetPlaceUseCase() {
    }

    public Single<Place> execute(String placeId) {
        if (placeId == null) throw new ArgumentNullException("placeId");

        return Single.<Place>create(e -> {
            placeRepository.get(placeId, e::onSuccess, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
