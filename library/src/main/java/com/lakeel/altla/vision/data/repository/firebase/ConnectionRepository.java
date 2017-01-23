package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.altla.vision.domain.helper.ObservableData;

public final class ConnectionRepository extends BaseDatabaseRepository {

    private static final String PATH_INFO_CONNECTED = ".info/connected";

    public ConnectionRepository(FirebaseDatabase database) {
        super(database);
    }

    public ObservableData<Boolean> observe() {
        DatabaseReference reference = getDatabase().getReference(PATH_INFO_CONNECTED);
        return new ObservableData<>(reference, snapshot -> snapshot.getValue(Boolean.class));
    }
}
