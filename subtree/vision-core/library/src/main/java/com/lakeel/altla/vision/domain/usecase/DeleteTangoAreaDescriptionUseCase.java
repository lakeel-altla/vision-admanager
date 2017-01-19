package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepository;

import javax.inject.Inject;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.schedulers.Schedulers;

public final class DeleteTangoAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    public DeleteTangoAreaDescriptionUseCase() {
    }

    public Completable execute(Tango tango, String areaDescriptionId) {
        if (tango == null) throw new ArgumentNullException("tango");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                tangoAreaDescriptionMetadataRepository.delete(tango, areaDescriptionId);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }
}
