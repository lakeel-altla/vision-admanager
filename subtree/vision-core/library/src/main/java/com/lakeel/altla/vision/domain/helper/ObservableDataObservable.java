package com.lakeel.altla.vision.domain.helper;

import com.lakeel.altla.vision.ArgumentNullException;

import rx.Observable;
import rx.functions.Func0;

public final class ObservableDataObservable {

    private ObservableDataObservable() {
    }

    public static <TData> Observable<TData> using(Func0<ObservableData<TData>> observableDataFactory) {
        if (observableDataFactory == null) throw new ArgumentNullException("observableDataFactory");

        return Observable.using(observableDataFactory,
                                observableData -> Observable.<TData>create(subscriber -> {
                                    observableData.observe(subscriber::onNext, subscriber::onError);
                                }),
                                ObservableData::close);
    }
}
