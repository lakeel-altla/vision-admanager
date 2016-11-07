package com.lakeel.altla.vision.admanager.data.repository;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;

public final class TangoMetaDataRepositoryImpl implements TangoMetaDataRepository {

    private final Tango mTango;

    private final MetaDataMapper mMapper = new MetaDataMapper();

    @Inject
    public TangoMetaDataRepositoryImpl(Tango tango) {
        mTango = tango;
    }

    @Override
    public Observable<AreaDescriptionMetaData> find(String uuid) {
        return Observable.just(mTango.loadAreaDescriptionMetaData(uuid))
                         .map(mMapper::fromTangoAreaDescriptionMetaData);
    }

    @Override
    public Observable<AreaDescriptionMetaData> findAll() {
        return Observable.from(mTango.listAreaDescriptions())
                         .flatMap(this::loadTangoAreaDescriptionMetaData)
                         .map(mMapper::fromTangoAreaDescriptionMetaData);
    }

    @Override
    public Single<String> delete(String uuid) {
        mTango.deleteAreaDescription(uuid);
        return Single.just(uuid);
    }

    Observable<TangoAreaDescriptionMetaData> loadTangoAreaDescriptionMetaData(String uuid) {
        return Observable.just(mTango.loadAreaDescriptionMetaData(uuid));
    }
}
