package com.lakeel.altla.vision.helper;

import com.google.firebase.database.DataSnapshot;

public interface DataSnapshotConverter<TData> {

    TData convert(DataSnapshot snapshot);
}
