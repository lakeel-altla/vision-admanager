package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.model.UserProfile;

public final class UserProfileRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserProfileRepository.class);

    private static final String PATH_USER_PROFILES = "userProfiles";

    public UserProfileRepository(FirebaseDatabase database) {
        super(database);
    }

    public void save(UserProfile userProfile) {
        if (userProfile == null) throw new ArgumentNullException("userProfile");

        getDatabase().getReference()
                     .child(PATH_USER_PROFILES)
                     .child(userProfile.userId)
                     .setValue(userProfile, (error, reference) -> {
                         if (error != null) {
                             LOG.e("Failed to save.", error.toException());
                         }
                     });
    }

    public void find(String userId, OnSuccessListener<UserProfile> onSuccessListener,
                     OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");

        getDatabase().getReference()
                     .child(PATH_USER_PROFILES)
                     .child(userId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserProfile userProfile = null;
                             if (snapshot.exists()) {
                                 userProfile = map(userId, snapshot);
                             }
                             onSuccessListener.onSuccess(userProfile);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public ObservableData<UserProfile> observe(String userId) {
        if (userId == null) throw new ArgumentNullException("userId");

        DatabaseReference reference = getDatabase().getReference()
                                                   .child(PATH_USER_PROFILES)
                                                   .child(userId);

        return new ObservableData<>(reference, snapshot -> map(userId, snapshot));
    }

    private UserProfile map(String userId, DataSnapshot snapshot) {
        UserProfile userProfile = snapshot.getValue(UserProfile.class);
        userProfile.userId = userId;
        return userProfile;
    }
}
