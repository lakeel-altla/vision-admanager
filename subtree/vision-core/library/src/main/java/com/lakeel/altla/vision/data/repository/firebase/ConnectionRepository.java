package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.rx.firebase.database.RxFirebaseQuery;

import rx.Observable;

public final class ConnectionRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(ConnectionRepository.class);

    private static final String PATH_INFO_CONNECTED = ".info/connected";

    public ConnectionRepository(FirebaseDatabase database) {
        super(database);
    }

    public Observable<Boolean> observe() {
        DatabaseReference reference = getDatabase().getReference(PATH_INFO_CONNECTED);

        return RxFirebaseQuery.asObservable(reference)
                              .map(snapshot -> snapshot.getValue(Boolean.class));
    }
}
