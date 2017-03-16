package com.lakeel.altla.vision.admanager.presentation.helper;

import com.lakeel.altla.vision.helper.DataListEvent;
import com.lakeel.altla.vision.helper.ObservableData;
import com.lakeel.altla.vision.helper.ObservableList;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

public final class RxHelper {

    private RxHelper() {
    }

    @NonNull
    public static <TData> Observable<TData> usingData(@NonNull Callable<ObservableData<TData>> observableDataFactory) {
        return Observable.using(observableDataFactory,
                                observableData -> Observable.<TData>create(subscriber -> {
                                    observableData.addOnDataChangeListener(subscriber::onNext);
                                    observableData.addOnFailureListener(subscriber::onError);
                                    observableData.observe();
                                }),
                                ObservableData::close);
    }

    @NonNull
    public static <TData> Observable<DataListEvent<TData>> usingList(
            @NonNull Callable<ObservableList<TData>> observableListFactory) {
        return Observable.using(observableListFactory,
                                observableList -> Observable.<DataListEvent<TData>>create(subscriber -> {
                                    observableList.addOnDataListEventListener(subscriber::onNext);
                                    observableList.addOnFailureListener(subscriber::onError);
                                    observableList.observe();
                                }),
                                ObservableList::close);
    }
}
