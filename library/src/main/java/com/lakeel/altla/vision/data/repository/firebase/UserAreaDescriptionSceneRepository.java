package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.model.UserAreaDescriptionScene;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class UserAreaDescriptionSceneRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserAreaDescriptionSceneRepository.class);

    private static final String BASE_PATH = "userAreaDescriptionScenes";

    public UserAreaDescriptionSceneRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserAreaDescriptionScene userAreaDescriptionScene) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userAreaDescriptionScene.userId)
                     .child(userAreaDescriptionScene.areaDescriptionId)
                     .child(userAreaDescriptionScene.sceneId)
                     .setValue(map(userAreaDescriptionScene), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String areaDescriptionId, @NonNull String sceneId,
                     OnSuccessListener<UserAreaDescriptionScene> onSuccessListener,
                     OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(areaDescriptionId)
                     .child(sceneId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserAreaDescriptionScene userAreaDescriptionScene = null;
                             if (snapshot.exists()) {
                                 userAreaDescriptionScene = map(userId, areaDescriptionId, snapshot);
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userAreaDescriptionScene);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void findAll(@NonNull String userId, @NonNull String areaDescriptionId,
                        OnSuccessListener<List<UserAreaDescriptionScene>> onSuccessListener,
                        OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(areaDescriptionId)
                     .orderByKey()
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             List<UserAreaDescriptionScene> list = new ArrayList<>((int) snapshot.getChildrenCount());
                             for (DataSnapshot child : snapshot.getChildren()) {
                                 list.add(map(userId, areaDescriptionId, child));
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(list);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void delete(@NonNull String userId, @NonNull String areaDescriptionId, @NonNull String sceneId) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(areaDescriptionId)
                     .child(sceneId)
                     .removeValue((error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to remove: reference = %s", reference), error.toException());
                         }
                     });
    }

    @NonNull
    private static Value map(@NonNull UserAreaDescriptionScene userAreaDescriptionScene) {
        Value value = new Value();
        value.translationX = userAreaDescriptionScene.translationX;
        value.translationY = userAreaDescriptionScene.translationY;
        value.translationZ = userAreaDescriptionScene.translationZ;
        value.orientationX = userAreaDescriptionScene.orientationX;
        value.orientationY = userAreaDescriptionScene.orientationY;
        value.orientationZ = userAreaDescriptionScene.orientationZ;
        value.orientationW = userAreaDescriptionScene.orientationW;
        return value;
    }

    @NonNull
    private static UserAreaDescriptionScene map(@NonNull String userId, @NonNull String areaDescriptionId,
                                                @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UserAreaDescriptionScene userAreaDescriptionScene = new UserAreaDescriptionScene(
                userId, areaDescriptionId, snapshot.getKey());
        userAreaDescriptionScene.translationX = value.translationX;
        userAreaDescriptionScene.translationY = value.translationY;
        userAreaDescriptionScene.translationZ = value.translationZ;
        userAreaDescriptionScene.orientationX = value.orientationX;
        userAreaDescriptionScene.orientationY = value.orientationY;
        userAreaDescriptionScene.orientationZ = value.orientationZ;
        userAreaDescriptionScene.orientationW = value.orientationW;
        return userAreaDescriptionScene;
    }

    public static final class Value {

        public float translationX;

        public float translationY;

        public float translationZ;

        public float orientationX;

        public float orientationY;

        public float orientationZ;

        public float orientationW;
    }
}
