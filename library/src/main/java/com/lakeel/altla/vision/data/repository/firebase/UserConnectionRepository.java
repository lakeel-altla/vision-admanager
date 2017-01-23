package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserConnection;

import java.util.HashMap;
import java.util.Map;

public final class UserConnectionRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserConnectionRepository.class);

    private static final String PATH_USER_CONNECTIONS = "userConnections";

    private static final String PATH_ONLINE = "online";

    private static final String PATH_LAST_ONLINE_TIME = "lastOnlineTime";

    public UserConnectionRepository(FirebaseDatabase database) {
        super(database);
    }

    public void markAsOnline(UserConnection userConnection) {
        if (userConnection == null) throw new ArgumentNullException("userConnection");

        DatabaseReference connectionReference = getDatabase().getReference()
                                                             .child(PATH_USER_CONNECTIONS)
                                                             .child(userConnection.userId)
                                                             .child(userConnection.instanceId);
        DatabaseReference onlineReference = connectionReference.child(PATH_ONLINE);
        DatabaseReference lastOnlineTimeReference = connectionReference.child(PATH_LAST_ONLINE_TIME);

        // when connected.
        onlineReference.setValue(Boolean.TRUE, (error, reference) -> {
            if (error != null) {
                LOG.e(String.format("Failed to do setValue: reference = %s", reference), error.toException());
            }
        });

        // when disconnected.
        onlineReference.onDisconnect().setValue(Boolean.FALSE, (error, reference) -> {
            if (error != null) {
                LOG.e(String.format("Failed to do setValue: reference = %s", reference), error.toException());
            }
        });
        lastOnlineTimeReference.onDisconnect().setValue(ServerValue.TIMESTAMP, (error, reference) -> {
            if (error != null) {
                LOG.e(String.format("Failed to do setValue: reference = %s", reference), error.toException());
            }
        });
    }

    public void markAsOffline(UserConnection userConnection) {
        if (userConnection == null) throw new ArgumentNullException("userConnection");

        DatabaseReference connectionReference = getDatabase().getReference()
                                                             .child(PATH_USER_CONNECTIONS)
                                                             .child(userConnection.userId)
                                                             .child(userConnection.instanceId);

        Map<String, Object> children = new HashMap<>(2);
        children.put(PATH_ONLINE, Boolean.FALSE);
        children.put(PATH_LAST_ONLINE_TIME, ServerValue.TIMESTAMP);

        connectionReference.updateChildren(children, (error, reference) -> {
            if (error != null) {
                LOG.e(String.format("Failed to do updateChildren: reference = %s", reference), error.toException());
            }
        });
    }
}
