package com.lakeel.altla.vision.data.repository.android;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;

import java.util.List;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;

public final class TangoAreaDescriptionMetadataRepositoryImpl implements TangoAreaDescriptionMetadataRepository {

    private final Tango tango;

    public TangoAreaDescriptionMetadataRepositoryImpl(Tango tango) {
        if (tango == null) throw new ArgumentNullException("tango");

        this.tango = tango;
    }

    @Override
    public Observable<TangoAreaDescriptionMetaData> find(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Observable.create(subscriber -> {
            TangoAreaDescriptionMetaData metaData = tango.loadAreaDescriptionMetaData(areaDescriptionId);
            subscriber.onNext(metaData);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<TangoAreaDescriptionMetaData> findAll() {
        return Observable.<String>create(subscriber -> {
            List<String> areaDescriptionIds = tango.listAreaDescriptions();
            for (String areaDescriptionId : areaDescriptionIds) {
                subscriber.onNext(areaDescriptionId);
            }
            subscriber.onCompleted();
        }).flatMap(this::find);
    }

    @Override
    public Completable delete(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber completableSubscriber) {
                tango.deleteAreaDescription(areaDescriptionId);
            }
        });
    }
}
