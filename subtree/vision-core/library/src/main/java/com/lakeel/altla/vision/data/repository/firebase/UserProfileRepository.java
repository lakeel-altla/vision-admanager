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
import com.lakeel.altla.vision.domain.model.UserProfile;

import android.support.annotation.NonNull;

public final class UserProfileRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserProfileRepository.class);

    private static final String PATH_USER_PROFILES = "userProfiles";

    public UserProfileRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserProfile userProfile) {
        getDatabase().getReference()
                     .child(PATH_USER_PROFILES)
                     .child(userProfile.userId)
                     .setValue(map(userProfile), (error, reference) -> {
                         if (error != null) {
                             LOG.e("Failed to save.", error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, OnSuccessListener<UserProfile> onSuccessListener,
                     OnFailureListener onFailureListener) {
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

    @NonNull
    public ObservableData<UserProfile> observe(@NonNull String userId) {
        DatabaseReference reference = getDatabase().getReference()
                                                   .child(PATH_USER_PROFILES)
                                                   .child(userId);

        return new ObservableData<>(reference, snapshot -> map(userId, snapshot));
    }

    @NonNull
    private static Value map(@NonNull UserProfile userProfile) {
        Value value = new Value();
        value.displayName = userProfile.displayName;
        value.email = userProfile.email;
        value.photoUri = userProfile.photoUri;
        value.createdAt = ServerTimestampMapper.map(userProfile.createdAt);
        value.updatedAt = ServerValue.TIMESTAMP;
        return value;
    }

    @NonNull
    private static UserProfile map(@NonNull String userId, @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UserProfile userProfile = new UserProfile(userId);
        userProfile.displayName = value.displayName;
        userProfile.email = value.email;
        userProfile.photoUri = value.photoUri;
        userProfile.createdAt = ServerTimestampMapper.map(value.createdAt);
        userProfile.updatedAt = ServerTimestampMapper.map(value.updatedAt);
        return userProfile;
    }

    public static final class Value {

        public String displayName;

        public String email;

        public String photoUri;

        public Object createdAt;

        public Object updatedAt;
    }
}
