package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.mapper.ServerTimestampMapper;
import com.lakeel.altla.vision.domain.model.UserActor;

import android.support.annotation.NonNull;

public final class UserActorRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserActorRepository.class);

    private static final String BASE_PATH = "userActors";

    public UserActorRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserActor userActor) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userActor.userId)
                     .child(userActor.sceneId)
                     .child(userActor.actorId)
                     .setValue(map(userActor), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String sceneId, @NonNull String actorId,
                     OnSuccessListener<UserActor> onSuccessListener, OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(sceneId)
                     .child(actorId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserActor userActor = null;
                             if (snapshot.exists()) {
                                 userActor = map(userId, sceneId, snapshot);
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userActor);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    @NonNull
    public ObservableDataList<UserActor> observeAll(@NonNull String userId, @NonNull String sceneId) {
        Query query = getDatabase().getReference()
                                   .child(BASE_PATH)
                                   .child(userId)
                                   .child(sceneId);

        return new ObservableDataList<>(query, snapshot -> map(userId, sceneId, snapshot));
    }

    @NonNull
    private static Value map(@NonNull UserActor userActor) {
        Value value = new Value();
        value.modelType = userActor.assetType.getValue();
        value.modelId = userActor.assetId;
        value.positionX = userActor.positionX;
        value.positionY = userActor.positionY;
        value.positionZ = userActor.positionZ;
        value.orientationX = userActor.orientationX;
        value.orientationY = userActor.orientationY;
        value.orientationZ = userActor.orientationZ;
        value.orientationW = userActor.orientationW;
        value.scaleX = userActor.scaleX;
        value.scaleY = userActor.scaleY;
        value.scaleZ = userActor.scaleZ;
        value.createdAt = ServerTimestampMapper.map(userActor.createdAt);
        value.updatedAt = ServerValue.TIMESTAMP;
        return value;
    }

    @NonNull
    private static UserActor map(@NonNull String userId, @NonNull String sceneId, @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UserActor userActor = new UserActor(userId, sceneId, snapshot.getKey());
        userActor.assetType = UserActor.AssetType.toModelType(value.modelType);
        userActor.assetId = value.modelId;
        userActor.positionX = value.positionX;
        userActor.positionY = value.positionY;
        userActor.positionZ = value.positionZ;
        userActor.orientationX = value.orientationX;
        userActor.orientationY = value.orientationY;
        userActor.orientationZ = value.orientationZ;
        userActor.orientationW = value.orientationW;
        userActor.scaleX = value.scaleX;
        userActor.scaleY = value.scaleY;
        userActor.scaleZ = value.scaleZ;
        userActor.createdAt = ServerTimestampMapper.map(value.createdAt);
        userActor.updatedAt = ServerTimestampMapper.map(value.updatedAt);
        return userActor;
    }

    public static final class Value {

        public int modelType;

        public String modelId;

        public double positionX;

        public double positionY;

        public double positionZ;

        public double orientationX;

        public double orientationY;

        public double orientationZ;

        public double orientationW;

        public double scaleX = 1;

        public double scaleY = 1;

        public double scaleZ = 1;

        public Object createdAt;

        public Object updatedAt;
    }
}
