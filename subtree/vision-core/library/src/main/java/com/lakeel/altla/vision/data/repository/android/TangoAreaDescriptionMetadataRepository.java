package com.lakeel.altla.vision.data.repository.android;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;

import java.util.List;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;

public final class TangoAreaDescriptionMetadataRepository {

    private static final Log LOG = LogFactory.getLog(TangoAreaDescriptionMetadataRepository.class);

    public Observable<TangoAreaDescriptionMetaData> find(Tango tango, String areaDescriptionId) {
        if (tango == null) throw new ArgumentNullException("tango");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Observable.create(subscriber -> {
            TangoAreaDescriptionMetaData metaData = tango.loadAreaDescriptionMetaData(areaDescriptionId);
            subscriber.onNext(metaData);
            subscriber.onCompleted();
        });
    }

    public Observable<TangoAreaDescriptionMetaData> findAll(Tango tango) {
        if (tango == null) throw new ArgumentNullException("tango");

        return Observable.<String>create(subscriber -> {
            List<String> areaDescriptionIds = tango.listAreaDescriptions();
            for (String areaDescriptionId : areaDescriptionIds) {
                subscriber.onNext(areaDescriptionId);
            }
            subscriber.onCompleted();
        }).flatMap(areaDescriptionId -> find(tango, areaDescriptionId));
    }

    public Completable delete(Tango tango, String areaDescriptionId) {
        if (tango == null) throw new ArgumentNullException("tango");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                tango.deleteAreaDescription(areaDescriptionId);

                subscriber.onCompleted();
            }
        });
    }
}
