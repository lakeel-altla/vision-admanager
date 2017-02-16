package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.mapper.ServerTimestampMapper;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class UserAreaDescriptionRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserAreaDescriptionRepository.class);

    private static final String PATH_USER_AREA_DESCRIPTIONS = "userAreaDescriptions";

    private static final String FIELD_NAME = "name";

    private static final String FIELD_AREA_ID = "areaId";

    public UserAreaDescriptionRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserAreaDescription userAreaDescription) {
        getDatabase().getReference()
                     .child(PATH_USER_AREA_DESCRIPTIONS)
                     .child(userAreaDescription.userId)
                     .child(userAreaDescription.areaDescriptionId)
                     .setValue(map(userAreaDescription), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String areaDescriptionId,
                     OnSuccessListener<UserAreaDescription> onSuccessListener, OnFailureListener onFailureListener) {
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

    public void findAll(@NonNull String userId, OnSuccessListener<List<UserAreaDescription>> onSuccessListener,
                        OnFailureListener onFailureListener) {
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

    public void findByAreaId(@NonNull String userId, @NonNull String areaId,
                             OnSuccessListener<List<UserAreaDescription>> onSuccessListener,
                             OnFailureListener onFailureListener) {
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

    @NonNull
    public ObservableData<UserAreaDescription> observe(@NonNull String userId, @NonNull String areaDescriptionId) {
        DatabaseReference reference = getDatabase().getReference()
                                                   .child(PATH_USER_AREA_DESCRIPTIONS)
                                                   .child(userId)
                                                   .child(areaDescriptionId);

        return new ObservableData<>(reference, snapshot -> map(userId, snapshot));
    }

    public void delete(@NonNull String userId, @NonNull String areaDescriptionId) {
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

    @NonNull
    private static Value map(@NonNull UserAreaDescription userAreaDescription) {
        Value value = new Value();
        value.name = userAreaDescription.name;
        value.fileUploaded = userAreaDescription.fileUploaded;
        value.areaId = userAreaDescription.areaId;
        value.createdAt = ServerTimestampMapper.map(userAreaDescription.createdAt);
        value.updatedAt = ServerValue.TIMESTAMP;
        return value;
    }

    @NonNull
    private static UserAreaDescription map(@NonNull String userId, @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UserAreaDescription userAreaDescription = new UserAreaDescription(userId, snapshot.getKey());
        userAreaDescription.name = value.name;
        userAreaDescription.fileUploaded = value.fileUploaded;
        userAreaDescription.areaId = value.areaId;
        userAreaDescription.createdAt = ServerTimestampMapper.map(value.createdAt);
        userAreaDescription.updatedAt = ServerTimestampMapper.map(value.updatedAt);
        return userAreaDescription;
    }

    public static final class Value {

        public String name;

        public boolean fileUploaded;

        public String areaId;

        public Object createdAt;

        public Object updatedAt;
    }
}
