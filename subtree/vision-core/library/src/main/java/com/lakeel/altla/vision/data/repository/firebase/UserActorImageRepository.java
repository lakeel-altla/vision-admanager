package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.mapper.ServerTimestampMapper;
import com.lakeel.altla.vision.domain.model.UserActorImage;

import android.support.annotation.NonNull;

public final class UserActorImageRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserActorImageRepository.class);

    private static final String BASE_PATH = "userActorImages";

    private static final String FIELD_NAME = "name";

    public UserActorImageRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserActorImage userActorImage) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userActorImage.userId)
                     .child(userActorImage.imageId)
                     .setValue(map(userActorImage), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String imageId,
                     OnSuccessListener<UserActorImage> onSuccessListener, OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(imageId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserActorImage userActorImage = null;
                             if (snapshot.exists()) {
                                 userActorImage = map(userId, snapshot);
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userActorImage);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    @NonNull
    public ObservableData<UserActorImage> observe(@NonNull String userId, @NonNull String imageId) {
        DatabaseReference reference = getDatabase().getReference()
                                                   .child(BASE_PATH)
                                                   .child(userId)
                                                   .child(imageId);

        return new ObservableData<>(reference, snapshot -> map(userId, snapshot));
    }

    @NonNull
    public ObservableDataList<UserActorImage> observeAll(@NonNull String userId) {
        Query query = getDatabase().getReference()
                                   .child(BASE_PATH)
                                   .child(userId)
                                   .orderByChild(FIELD_NAME);

        return new ObservableDataList<>(query, snapshot -> map(userId, snapshot));
    }

    @NonNull
    private static Value map(@NonNull UserActorImage userActorImage) {
        Value value = new Value();
        value.name = userActorImage.name;
        value.fileUploaded = userActorImage.fileUploaded;
        value.createdAt = ServerTimestampMapper.map(userActorImage.createdAt);
        value.updatedAt = ServerValue.TIMESTAMP;
        return value;
    }

    @NonNull
    private static UserActorImage map(@NonNull String userId, @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UserActorImage userActorImage = new UserActorImage(userId, snapshot.getKey());
        userActorImage.name = value.name;
        userActorImage.fileUploaded = value.fileUploaded;
        userActorImage.createdAt = ServerTimestampMapper.map(value.createdAt);
        userActorImage.updatedAt = ServerTimestampMapper.map(value.updatedAt);
        return userActorImage;
    }

    public static final class Value {

        public String name;

        public boolean fileUploaded;

        public Object createdAt;

        public Object updatedAt;
    }
}
