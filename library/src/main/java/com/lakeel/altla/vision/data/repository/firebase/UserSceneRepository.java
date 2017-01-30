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
import com.lakeel.altla.vision.domain.model.UserScene;

import java.util.ArrayList;
import java.util.List;

public final class UserSceneRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserSceneRepository.class);

    private static final String PATH_USER_SCENES = "userScenes";

    private static final String FIELD_NAME = "name";

    public UserSceneRepository(FirebaseDatabase database) {
        super(database);
    }

    public void save(UserScene userScene) {
        if (userScene == null) throw new ArgumentNullException("userScene");

        getDatabase().getReference()
                     .child(PATH_USER_SCENES)
                     .child(userScene.userId)
                     .child(userScene.sceneId)
                     .setValue(userScene, (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(String userId, String sceneId, OnSuccessListener<UserScene> onSuccessListener,
                     OnFailureListener onFailureListener) {
        if (sceneId == null) throw new ArgumentNullException("sceneId");

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

    public void findAll(String userId, OnSuccessListener<List<UserScene>> onSuccessListener,
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

    public void delete(String userId, String sceneId) {
        if (sceneId == null) throw new ArgumentNullException("sceneId");

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

    private UserScene map(String userId, DataSnapshot snapshot) {
        UserScene userScene = snapshot.getValue(UserScene.class);
        userScene.userId = userId;
        userScene.sceneId = snapshot.getKey();
        return userScene;
    }
}
