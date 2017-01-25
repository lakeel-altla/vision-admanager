package com.lakeel.altla.vision.data.repository.android;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;

public final class PlaceRepository {

    private final GoogleApiClient googleApiClient;

    public PlaceRepository(GoogleApiClient googleApiClient) {
        if (googleApiClient == null) throw new ArgumentNullException("googleApiClient");

        this.googleApiClient = googleApiClient;
    }

    public void get(String placeId, OnSuccessListener<Place> onSuccessListener, OnFailureListener onFailureListener) {
        if (placeId == null) throw new ArgumentNullException("placeId");

        Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
                         .setResultCallback(places -> {
                             if (places.getStatus().isSuccess()) {
                                 Place place = places.get(0);

                                 if (onSuccessListener != null) onSuccessListener.onSuccess(place);
                             } else if (places.getStatus().isCanceled()) {
                                 if (onFailureListener != null) {
                                     onFailureListener.onFailure(new StatusException("Cancelled.", placeId));
                                 }
                             } else if (places.getStatus().isInterrupted()) {
                                 if (onFailureListener != null) {
                                     onFailureListener.onFailure(new StatusException("Interrupted.", placeId));
                                 }
                             } else {
                                 if (onFailureListener != null) {
                                     onFailureListener.onFailure(new StatusException("Unexpected Error.", placeId));
                                 }
                             }
                         });
    }

    public final class StatusException extends RuntimeException {

        private final String placeId;

        private StatusException(String message, String placeId) {
            super(message);
            this.placeId = placeId;
        }

        public String getPlaceId() {
            return placeId;
        }
    }
}
