package com.lakeel.altla.vision.domain.helper;

import com.lakeel.altla.vision.ArgumentNullException;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

public final class ObservableDataObservable {

    private ObservableDataObservable() {
    }

    public static <TData> Observable<TData> using(Callable<ObservableData<TData>> observableDataFactory) {
        if (observableDataFactory == null) throw new ArgumentNullException("observableDataFactory");

        return Observable.using(observableDataFactory,
                                observableData -> Observable.<TData>create(subscriber -> {
                                    observableData.observe(subscriber::onNext, subscriber::onError);
                                }),
                                ObservableData::close);
    }
}
