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
import com.lakeel.altla.vision.domain.model.UserScene;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class UserSceneRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserSceneRepository.class);

    private static final String PATH_USER_SCENES = "userScenes";

    private static final String FIELD_NAME = "name";

    private static final String FIELD_AREA_ID = "areaId";

    public UserSceneRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserScene userScene) {
        getDatabase().getReference()
                     .child(PATH_USER_SCENES)
                     .child(userScene.userId)
                     .child(userScene.sceneId)
                     .setValue(map(userScene), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String sceneId, OnSuccessListener<UserScene> onSuccessListener,
                     OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(PATH_USER_SCENES)
                     .child(userId)
                     .child(sceneId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserScene userScene = null;
                             if (snapshot.exists()) {
                                 userScene = map(userId, snapshot);
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userScene);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void findAll(@NonNull String userId, OnSuccessListener<List<UserScene>> onSuccessListener,
                        OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(PATH_USER_SCENES)
                     .child(userId)
                     .orderByChild(FIELD_NAME)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             List<UserScene> list = new ArrayList<>((int) snapshot.getChildrenCount());
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
                             OnSuccessListener<List<UserScene>> onSuccessListener,
                             OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(PATH_USER_SCENES)
                     .child(userId)
                     .orderByChild(FIELD_AREA_ID)
                     .equalTo(areaId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             List<UserScene> list = new ArrayList<>((int) snapshot.getChildrenCount());
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
    public ObservableData<UserScene> observe(@NonNull String userId, @NonNull String sceneId) {
        DatabaseReference reference = getDatabase().getReference()
                                                   .child(PATH_USER_SCENES)
                                                   .child(userId)
                                                   .child(sceneId);

        return new ObservableData<>(reference, snapshot -> map(userId, snapshot));
    }

    public void delete(@NonNull String userId, @NonNull String sceneId) {
        getDatabase().getReference()
                     .child(PATH_USER_SCENES)
                     .child(userId)
                     .child(sceneId)
                     .removeValue((error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to remove: reference = %s", reference), error.toException());
                         }
                     });
    }

    @NonNull
    public static Value map(@NonNull UserScene userScene) {
        Value value = new Value();
        value.name = userScene.name;
        value.areaId = userScene.areaId;
        value.createdAt = ServerTimestampMapper.map(userScene.createdAt);
        value.updatedAt = ServerValue.TIMESTAMP;
        return value;
    }

    @NonNull
    private static UserScene map(@NonNull String userId, @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UserScene userScene = new UserScene(userId, snapshot.getKey());
        userScene.name = value.name;
        userScene.areaId = value.areaId;
        userScene.createdAt = ServerTimestampMapper.map(value.createdAt);
        userScene.updatedAt = ServerTimestampMapper.map(value.updatedAt);
        return userScene;
    }

    public static final class Value {

        public String name;

        public String areaId;

        public Object createdAt;

        public Object updatedAt;
    }
}
