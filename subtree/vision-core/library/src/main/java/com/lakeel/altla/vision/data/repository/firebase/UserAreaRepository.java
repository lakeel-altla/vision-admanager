package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.mapper.ServerTimestampMapper;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class UserAreaRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserAreaRepository.class);

    private static final String BASE_PATH = "userAreas";

    private static final String FIELD_NAME = "name";

    public UserAreaRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserArea userArea) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userArea.userId)
                     .child(userArea.areaId)
                     .setValue(map(userArea), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String areaId, OnSuccessListener<UserArea> onSuccessListener,
                     OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(areaId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserArea userArea = null;
                             if (snapshot.exists()) {
                                 userArea = map(userId, snapshot);
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userArea);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void findAll(@NonNull String userId, OnSuccessListener<List<UserArea>> onSuccessListener,
                        OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .orderByChild(FIELD_NAME)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             List<UserArea> list = new ArrayList<>((int) snapshot.getChildrenCount());
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

    public void delete(@NonNull String userId, @NonNull String areaId) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(areaId)
                     .removeValue((error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to remove: reference = %s", reference), error.toException());
                         }
                     });
    }

    @NonNull
    private static Value map(@NonNull UserArea userArea) {
        Value value = new Value();
        value.name = userArea.name;
        value.placeId = userArea.placeId;
        value.level = userArea.level;
        value.createdAt = ServerTimestampMapper.map(userArea.createdAt);
        value.updatedAt = ServerValue.TIMESTAMP;
        return value;
    }

    @NonNull
    private static UserArea map(@NonNull String userId, @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UserArea userArea = new UserArea(userId, snapshot.getKey());
        userArea.name = value.name;
        userArea.placeId = value.placeId;
        userArea.level = value.level;
        userArea.createdAt = ServerTimestampMapper.map(value.createdAt);
        userArea.updatedAt = ServerTimestampMapper.map(value.updatedAt);
        return userArea;
    }

    public static final class Value {

        public String name;

        public String placeId;

        public int level;

        public Object createdAt;

        public Object updatedAt;
    }
}
