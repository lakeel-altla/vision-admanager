package com.lakeel.altla.vision.helper;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;

import android.support.annotation.NonNull;

import java.io.Closeable;

public final class ObservableList<TData> implements Closeable {

    private static final Log LOG = LogFactory.getLog(ObservableList.class);

    private final Query query;

    private final DataSnapshotConverter<TData> dataSnapshotConverter;

    private ChildEventListener childEventListener;

    private boolean closed;

    public ObservableList(@NonNull Query query, @NonNull DataSnapshotConverter<TData> dataSnapshotConverter) {
        this.query = query;
        this.dataSnapshotConverter = dataSnapshotConverter;
    }

    public void observe(OnDataListEventListener<TData> onDataListEventListener, OnFailureListener onFailureListener) {
        if (closed) throw new IllegalStateException("This object is already closed.");

        if (childEventListener != null) {
            query.removeEventListener(childEventListener);
            childEventListener = null;
        }

        if (onDataListEventListener != null || onFailureListener != null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                    TData data = dataSnapshotConverter.convert(snapshot);
                    if (onDataListEventListener != null) {
                        onDataListEventListener.onDataListEvent(
                                new DataListEvent<>(DataListEvent.Type.ADDED, data, previousChildName));
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                    TData data = dataSnapshotConverter.convert(snapshot);
                    if (onDataListEventListener != null) {
                        onDataListEventListener.onDataListEvent(
                                new DataListEvent<>(DataListEvent.Type.CHANGED, data, previousChildName));
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot snapshot) {
                    TData data = dataSnapshotConverter.convert(snapshot);
                    if (onDataListEventListener != null) {
                        onDataListEventListener.onDataListEvent(
                                new DataListEvent<>(DataListEvent.Type.REMOVED, data, null));
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                    TData data = dataSnapshotConverter.convert(snapshot);
                    if (onDataListEventListener != null) {
                        onDataListEventListener.onDataListEvent(
                                new DataListEvent<>(DataListEvent.Type.MOVED, data, previousChildName));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                }
            };
            query.addChildEventListener(childEventListener);
        }
    }

    @Override
    public void close() {
        if (childEventListener != null) {
            query.removeEventListener(childEventListener);
            childEventListener = null;
        }

        closed = true;

        LOG.v("Closed.");
    }

    public interface DataSnapshotConverter<TData> {

        TData convert(DataSnapshot snapshot);
    }
}
