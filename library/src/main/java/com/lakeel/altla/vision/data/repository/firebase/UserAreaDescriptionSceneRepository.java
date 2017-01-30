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
import com.lakeel.altla.vision.domain.model.UserAreaDescriptionScene;

import java.util.ArrayList;
import java.util.List;

public final class UserAreaDescriptionSceneRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserAreaDescriptionSceneRepository.class);

    private static final String BASE_PATH = "userAreaDescriptionScenes";

    public UserAreaDescriptionSceneRepository(FirebaseDatabase database) {
        super(database);
    }

    public void save(UserAreaDescriptionScene userAreaDescriptionScene) {
        if (userAreaDescriptionScene == null) throw new ArgumentNullException("userAreaDescriptionScene");

        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userAreaDescriptionScene.userId)
                     .child(userAreaDescriptionScene.areaDescriptionId)
                     .child(userAreaDescriptionScene.sceneId)
                     .setValue(userAreaDescriptionScene, (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(String userId, String areaDescriptionId, String sceneId,
                     OnSuccessListener<UserAreaDescriptionScene> onSuccessListener,
                     OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");
        if (sceneId == null) throw new ArgumentNullException("sceneId");

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

    public void findAll(String userId, String areaDescriptionId,
                        OnSuccessListener<List<UserAreaDescriptionScene>> onSuccessListener,
                        OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

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

    public void delete(String userId, String areaDescriptionId, String sceneId) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");
        if (sceneId == null) throw new ArgumentNullException("sceneId");

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

    private UserAreaDescriptionScene map(String userId, String areaDescriptionId, DataSnapshot snapshot) {
        UserAreaDescriptionScene userAreaDescriptionScene = snapshot.getValue(UserAreaDescriptionScene.class);
        userAreaDescriptionScene.userId = userId;
        userAreaDescriptionScene.areaDescriptionId = areaDescriptionId;
        userAreaDescriptionScene.sceneId = snapshot.getKey();
        return userAreaDescriptionScene;
    }
}
