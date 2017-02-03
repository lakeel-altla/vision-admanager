package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import java.util.ArrayList;
import java.util.List;

public final class UserAreaDescriptionRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserAreaDescriptionRepository.class);

    private static final String PATH_USER_AREA_DESCRIPTIONS = "userAreaDescriptions";

    private static final String FIELD_NAME = "name";

    private static final String FIELD_AREA_ID = "areaId";

    public UserAreaDescriptionRepository(FirebaseDatabase database) {
        super(database);
    }

    public void save(UserAreaDescription userAreaDescription) {
        if (userAreaDescription == null) throw new ArgumentNullException("userAreaDescription");

        getDatabase().getReference()
                     .child(PATH_USER_AREA_DESCRIPTIONS)
                     .child(userAreaDescription.userId)
                     .child(userAreaDescription.areaDescriptionId)
                     .setValue(userAreaDescription, (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(String userId, String areaDescriptionId, OnSuccessListener<UserAreaDescription> onSuccessListener,
                     OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        getDatabase().getReference()
                     .child(PATH_USER_AREA_DESCRIPTIONS)
                     .child(userId)
                     .child(areaDescriptionId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserAreaDescription userAreaDescription = null;
                             if (snapshot.exists()) {
                                 userAreaDescription = map(userId, snapshot);
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userAreaDescription);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void findAll(String userId, OnSuccessListener<List<UserAreaDescription>> onSuccessListener,
                        OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");

        getDatabase().getReference()
                     .child(PATH_USER_AREA_DESCRIPTIONS)
                     .child(userId)
                     .orderByChild(FIELD_NAME)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             List<UserAreaDescription> list = new ArrayList<>((int) snapshot.getChildrenCount());
                             for (DataSnapshot child : snapshot.getChildren()) {
                                 list.add(map(userId, child));
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(list);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void findByAreaId(String userId, String areaId,
                             OnSuccessListener<List<UserAreaDescription>> onSuccessListener,
                             OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaId == null) throw new ArgumentNullException("areaId");

        getDatabase().getReference()
                     .child(PATH_USER_AREA_DESCRIPTIONS)
                     .child(userId)
                     .orderByChild(FIELD_AREA_ID)
                     .equalTo(areaId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             List<UserAreaDescription> list = new ArrayList<>((int) snapshot.getChildrenCount());
                             for (DataSnapshot child : snapshot.getChildren()) {
                                 list.add(map(userId, child));
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(list);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void delete(String userId, String areaDescriptionId) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        getDatabase().getReference()
                     .child(PATH_USER_AREA_DESCRIPTIONS)
                     .child(userId)
                     .child(areaDescriptionId)
                     .removeValue((error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to remove: reference = %s", reference), error.toException());
                         }
                     });
    }

    private UserAreaDescription map(String userId, DataSnapshot snapshot) {
        UserAreaDescription userAreaDescription = snapshot.getValue(UserAreaDescription.class);
        userAreaDescription.userId = userId;
        userAreaDescription.areaDescriptionId = snapshot.getKey();
        return userAreaDescription;
    }
}
