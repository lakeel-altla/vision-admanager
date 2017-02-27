package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.mapper.ServerTimestampMapper;
import com.lakeel.altla.vision.domain.model.UserAssetImageFileUploadTask;

import android.support.annotation.NonNull;

public final class UserAssetImageFileUploadTaskRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserAssetImageFileUploadTaskRepository.class);

    private static final String BASE_PATH = "userAssetImageFileUploadTasks";

    private static final String FIELD_ORDER = "order";

    public UserAssetImageFileUploadTaskRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserAssetImageFileUploadTask userAssetImageFileUploadTask) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userAssetImageFileUploadTask.userId)
                     .child(userAssetImageFileUploadTask.assetId)
                     .setValue(map(userAssetImageFileUploadTask), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    @NonNull
    public ObservableDataList<UserAssetImageFileUploadTask> observeAll(@NonNull String userId) {
        Query query = getDatabase().getReference()
                                   .child(BASE_PATH)
                                   .child(userId)
                                   .orderByChild(FIELD_ORDER);

        return new ObservableDataList<>(query, snapshot -> map(userId, snapshot));
    }

    public void delete(@NonNull String userId, @NonNull String assetId) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(assetId)
                     .removeValue((error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to remove: reference = %s", reference), error.toException());
                         }
                     });
    }

    @NonNull
    private static Value map(@NonNull UserAssetImageFileUploadTask userAssetImageFileUploadTask) {
        Value value = new Value();
        value.instanceId = userAssetImageFileUploadTask.instanceId;
        value.sourceUri = userAssetImageFileUploadTask.sourceUriString;
        value.createdAt = ServerTimestampMapper.map(userAssetImageFileUploadTask.createdAt);
        value.order = -System.currentTimeMillis();
        return value;
    }

    @NonNull
    private static UserAssetImageFileUploadTask map(@NonNull String userId, @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UserAssetImageFileUploadTask task = new UserAssetImageFileUploadTask(userId, snapshot.getKey());
        task.instanceId = value.instanceId;
        task.sourceUriString = value.sourceUri;
        task.createdAt = ServerTimestampMapper.map(value.createdAt);
        return task;
    }

    public static final class Value {

        public String instanceId;

        public String sourceUri;

        public Object createdAt;

        // to sort in desc order.
        public long order;
    }
}
