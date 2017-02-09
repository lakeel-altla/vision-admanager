package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepository;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DeleteTangoAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    public DeleteTangoAreaDescriptionUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull Tango tango, @NonNull String areaDescriptionId) {
        return Completable.create(e -> {
            tangoAreaDescriptionMetadataRepository.delete(tango, areaDescriptionId);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
