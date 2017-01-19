package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.rx.firebase.database.RxFirebaseQuery;
import com.lakeel.altla.vision.ArgumentNullException;

import rx.Observable;

public final class ConnectionRepository {

    private static final Log LOG = LogFactory.getLog(ConnectionRepository.class);

    private static final String PATH_INFO_CONNECTED = ".info/connected";

    private final FirebaseDatabase database;

    public ConnectionRepository(FirebaseDatabase database) {
        if (database == null) throw new ArgumentNullException("database");

        this.database = database;
    }

    public Observable<Boolean> observe() {
        DatabaseReference reference = database.getReference(PATH_INFO_CONNECTED);

        return RxFirebaseQuery.asObservable(reference)
                              .map(snapshot -> snapshot.getValue(Boolean.class));
    }
}
