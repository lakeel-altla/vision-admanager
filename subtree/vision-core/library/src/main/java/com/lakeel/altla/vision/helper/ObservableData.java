package com.lakeel.altla.vision.helper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;

import android.support.annotation.NonNull;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public final class ObservableData<TData> implements Closeable {

    private static final Log LOG = LogFactory.getLog(ObservableData.class);

    private final Query query;

    private final DataSnapshotConverter<TData> dataSnapshotConverter;

    private final List<OnDataChangeListener<TData>> onDataChangeListeners = new ArrayList<>();

    private final List<OnFailureListener> onFailureListeners = new ArrayList<>();

    private ValueEventListener valueEventListener;

    private boolean closed;

    public ObservableData(@NonNull Query query, @NonNull DataSnapshotConverter<TData> dataSnapshotConverter) {
        this.query = query;
        this.dataSnapshotConverter = dataSnapshotConverter;
    }

    public void addOnDataChangeListener(@NonNull OnDataChangeListener<TData> dataChangeListener) {
        onDataChangeListeners.add(dataChangeListener);
    }

    public void removeOnDataChangeListener(@NonNull OnDataChangeListener<TData> dataChangeListener) {
        onDataChangeListeners.remove(dataChangeListener);
    }

    public void addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        onFailureListeners.add(onFailureListener);
    }

    public void removeOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        onFailureListeners.remove(onFailureListener);
    }

    public void observe() {
        if (closed) throw new IllegalStateException("This object is closed.");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                TData data = null;
                if (snapshot.exists()) {
                    data = dataSnapshotConverter.convert(snapshot);
                }
                if (data == null) {
                    LOG.w("The target data not found.");
                } else {
                    for (OnDataChangeListener<TData> onDataChangeListener : onDataChangeListeners) {
                        onDataChangeListener.onDataChange(data);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Exception e = error.toException();
                for (OnFailureListener onFailureListener : onFailureListeners) {
                    onFailureListener.onFailure(e);
                }
            }
        };
        query.addValueEventListener(valueEventListener);
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
}
