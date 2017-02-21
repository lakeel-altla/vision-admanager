package com.lakeel.altla.vision.domain.helper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;

import android.support.annotation.NonNull;

import java.io.Closeable;
import java.util.concurrent.Callable;

import io.reactivex.Observable;

public final class ObservableData<TData> implements Closeable {

    private static final Log LOG = LogFactory.getLog(ObservableData.class);

    private final Query query;

    private final DataSnapshotConverter<TData> dataSnapshotConverter;

    private ValueEventListener valueEventListener;

    private boolean closed;

    public ObservableData(@NonNull Query query, @NonNull DataSnapshotConverter<TData> dataSnapshotConverter) {
        this.query = query;
        this.dataSnapshotConverter = dataSnapshotConverter;
    }

    @NonNull
    public static <TData> Observable<TData> using(@NonNull Callable<ObservableData<TData>> observableDataFactory) {
        return Observable.using(observableDataFactory,
                                observableData -> Observable.<TData>create(subscriber -> {
                                    observableData.observe(subscriber::onNext, subscriber::onError);
                                }),
                                ObservableData::close);
    }

    public void observe(OnDataChangeListener<TData> onDataChangeListener, OnFailureListener onFailureListener) {
        if (closed) throw new IllegalStateException("This object is already closed.");

        if (valueEventListener != null) {
            query.removeEventListener(valueEventListener);
            valueEventListener = null;
        }

        if (onDataChangeListener != null || onFailureListener != null) {
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    TData data = null;
                    if (snapshot.exists()) {
                        data = dataSnapshotConverter.convert(snapshot);
                    }
                    if (onDataChangeListener != null) onDataChangeListener.onDataChange(data);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                }
            };
            query.addValueEventListener(valueEventListener);
        }
    }

    @Override
    public void close() {
        if (valueEventListener != null) {
            query.removeEventListener(valueEventListener);
            valueEventListener = null;
        }

        closed = true;

        LOG.v("Closed.");
    }

    public interface DataSnapshotConverter<TData> {

        TData convert(DataSnapshot snapshot);
    }
}
