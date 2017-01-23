package com.lakeel.altla.vision.domain.helper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.vision.ArgumentNullException;

import java.io.Closeable;

public final class ObservableData<TData> implements Closeable {

    private final Query query;

    private final DataSnapshotConverter<TData> dataSnapshotConverter;

    private ValueEventListener valueEventListener;

    private boolean closed;

    public ObservableData(Query query, DataSnapshotConverter<TData> dataSnapshotConverter) {
        if (query == null) throw new ArgumentNullException("query");
        if (dataSnapshotConverter == null) throw new ArgumentNullException("dataSnapshotConverter");

        this.query = query;
        this.dataSnapshotConverter = dataSnapshotConverter;
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
    }

    public interface DataSnapshotConverter<TData> {

        TData convert(DataSnapshot snapshot);
    }
}
