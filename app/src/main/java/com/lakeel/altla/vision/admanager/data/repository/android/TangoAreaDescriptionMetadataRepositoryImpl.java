package com.lakeel.altla.vision.admanager.data.repository.android;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.repository.TangoAreaDescriptionMetadataRepository;

import rx.Observable;
import rx.Single;

public final class TangoAreaDescriptionMetadataRepositoryImpl implements TangoAreaDescriptionMetadataRepository {

    private final Tango tango;

    public TangoAreaDescriptionMetadataRepositoryImpl(Tango tango) {
        if (tango == null) throw new ArgumentNullException("tango");

        this.tango = tango;
    }

    @Override
    public Observable<TangoAreaDescriptionMetaData> find(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return Observable.just(tango.loadAreaDescriptionMetaData(uuid));
    }

    @Override
    public Observable<TangoAreaDescriptionMetaData> findAll() {
        return Observable.from(tango.listAreaDescriptions())
                         .flatMap(this::find);
    }

    @Override
    public Single<String> delete(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return Single.create(subscriber -> {
            tango.deleteAreaDescription(uuid);
            subscriber.onSuccess(uuid);
        });
    }
}
