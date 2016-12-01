package com.lakeel.altla.vision.admanager.data.repository.android;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.data.repository.mapper.MetaDataMapper;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;

public final class TangoMetaDataRepositoryImpl implements TangoMetaDataRepository {

    private final Tango tango;

    private final MetaDataMapper mapper = new MetaDataMapper();

    @Inject
    public TangoMetaDataRepositoryImpl(Tango tango) {
        if (tango == null) throw new ArgumentNullException("tango");

        this.tango = tango;
    }

    @Override
    public Observable<AreaDescriptionMetaData> find(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return Observable.just(tango.loadAreaDescriptionMetaData(uuid))
                         .map(mapper::fromTangoAreaDescriptionMetaData);
    }

    @Override
    public Observable<AreaDescriptionMetaData> findAll() {
        return Observable.from(tango.listAreaDescriptions())
                         .flatMap(this::loadTangoAreaDescriptionMetaData)
                         .map(mapper::fromTangoAreaDescriptionMetaData);
    }

    @Override
    public Single<String> delete(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        tango.deleteAreaDescription(uuid);
        return Single.just(uuid);
    }

    private Observable<TangoAreaDescriptionMetaData> loadTangoAreaDescriptionMetaData(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return Observable.just(tango.loadAreaDescriptionMetaData(uuid));
    }
}
