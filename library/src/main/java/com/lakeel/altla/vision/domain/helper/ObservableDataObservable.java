package com.lakeel.altla.vision.domain.helper;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

public final class ObservableDataObservable {

    private ObservableDataObservable() {
    }

    @NonNull
    public static <TData> Observable<TData> using(@NonNull Callable<ObservableData<TData>> observableDataFactory) {
        return Observable.using(observableDataFactory,
                                observableData -> Observable.<TData>create(subscriber -> {
                                    observableData.observe(subscriber::onNext, subscriber::onError);
                                }),
                                ObservableData::close);
    }
}
