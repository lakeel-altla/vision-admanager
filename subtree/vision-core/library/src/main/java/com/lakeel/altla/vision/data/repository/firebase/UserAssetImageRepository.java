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
import com.lakeel.altla.vision.domain.model.UserAssetImage;

import android.support.annotation.NonNull;

public final class UserAssetImageRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserAssetImageRepository.class);

    private static final String BASE_PATH = "userAssetImages";

    private static final String FIELD_NAME = "name";

    public UserAssetImageRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserAssetImage userAssetImage) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userAssetImage.userId)
                     .child(userAssetImage.assetId)
                     .setValue(map(userAssetImage), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String assetId,
                     OnSuccessListener<UserAssetImage> onSuccessListener, OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(assetId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserAssetImage userAssetImage = null;
                             if (snapshot.exists()) {
                                 userAssetImage = map(userId, snapshot);
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userAssetImage);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    @NonNull
    public ObservableData<UserAssetImage> observe(@NonNull String userId, @NonNull String assetId) {
        DatabaseReference reference = getDatabase().getReference()
                                                   .child(BASE_PATH)
                                                   .child(userId)
                                                   .child(assetId);

        return new ObservableData<>(reference, snapshot -> map(userId, snapshot));
    }

    @NonNull
    public ObservableDataList<UserAssetImage> observeAll(@NonNull String userId) {
        Query query = getDatabase().getReference()
                                   .child(BASE_PATH)
                                   .child(userId)
                                   .orderByChild(FIELD_NAME);

        return new ObservableDataList<>(query, snapshot -> map(userId, snapshot));
    }

    @NonNull
    private static Value map(@NonNull UserAssetImage userAssetImage) {
        Value value = new Value();
        value.name = userAssetImage.name;
        value.fileUploaded = userAssetImage.fileUploaded;
        value.createdAt = ServerTimestampMapper.map(userAssetImage.createdAt);
        value.updatedAt = ServerValue.TIMESTAMP;
        return value;
    }

    @NonNull
    private static UserAssetImage map(@NonNull String userId, @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UserAssetImage userAssetImage = new UserAssetImage(userId, snapshot.getKey());
        userAssetImage.name = value.name;
        userAssetImage.fileUploaded = value.fileUploaded;
        userAssetImage.createdAt = ServerTimestampMapper.map(value.createdAt);
        userAssetImage.updatedAt = ServerTimestampMapper.map(value.updatedAt);
        return userAssetImage;
    }

    public static final class Value {

        public String name;

        public boolean fileUploaded;

        public Object createdAt;

        public Object updatedAt;
    }
}
