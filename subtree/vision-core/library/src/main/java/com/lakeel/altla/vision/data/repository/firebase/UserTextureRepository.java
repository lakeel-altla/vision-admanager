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
import com.lakeel.altla.vision.domain.model.UserTexture;

import java.util.ArrayList;
import java.util.List;

public final class UserTextureRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserTextureRepository.class);

    private static final String PATH_USER_TEXTURES = "userTextures";

    private static final String FIELD_NAME = "name";

    public UserTextureRepository(FirebaseDatabase database) {
        super(database);
    }

    public void save(UserTexture userTexture) {
        if (userTexture == null) throw new ArgumentNullException("userTexture");

        getDatabase().getReference()
                     .child(PATH_USER_TEXTURES)
                     .child(userTexture.userId)
                     .child(userTexture.textureId)
                     .setValue(userTexture, (error, reference) -> {
                         if (error != null) {
                             LOG.e("Failed to save.", error.toException());
                         }
                     });
    }

    public void find(String userId, String textureId, OnSuccessListener<UserTexture> onSuccessListener,
                     OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (textureId == null) throw new ArgumentNullException("textureId");

        getDatabase().getReference()
                     .child(PATH_USER_TEXTURES)
                     .child(userId)
                     .child(textureId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserTexture userTexture = null;
                             if (snapshot.exists()) {
                                 userTexture = map(userId, snapshot);
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userTexture);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void findAll(String userId, OnSuccessListener<List<UserTexture>> onSuccessListener,
                        OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");

        getDatabase().getReference()
                     .child(PATH_USER_TEXTURES)
                     .child(userId)
                     .orderByChild(FIELD_NAME)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             List<UserTexture> userTextures = new ArrayList<>((int) snapshot.getChildrenCount());
                             for (DataSnapshot child : snapshot.getChildren()) {
                                 userTextures.add(map(userId, child));
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userTextures);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void delete(String userId, String textureId) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (textureId == null) throw new ArgumentNullException("textureId");

        getDatabase().getReference()
                     .child(PATH_USER_TEXTURES)
                     .child(userId)
                     .child(textureId)
                     .removeValue((error, reference) -> {
                         if (error != null) {
                             LOG.e("Failed to delete.", error.toException());
                         }
                     });
    }

    private UserTexture map(String userId, DataSnapshot snapshot) {
        UserTexture userTexture = snapshot.getValue(UserTexture.class);
        userTexture.userId = userId;
        userTexture.textureId = snapshot.getKey();
        return userTexture;
    }
}
