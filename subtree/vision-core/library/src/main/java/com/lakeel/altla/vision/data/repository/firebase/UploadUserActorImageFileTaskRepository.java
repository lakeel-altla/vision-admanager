package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.mapper.ServerTimestampMapper;
import com.lakeel.altla.vision.domain.model.UploadUserActorImageFileTask;

import android.support.annotation.NonNull;

public final class UploadUserActorImageFileTaskRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UploadUserActorImageFileTaskRepository.class);

    private static final String BASE_PATH = "uploadActorImageFileTasks";

    private static final String FIELD_ORDER = "order";

    public UploadUserActorImageFileTaskRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UploadUserActorImageFileTask uploadUserActorImageFileTask) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(uploadUserActorImageFileTask.userId)
                     .child(uploadUserActorImageFileTask.imageId)
                     .setValue(map(uploadUserActorImageFileTask), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    @NonNull
    public ObservableDataList<UploadUserActorImageFileTask> observeAll(@NonNull String userId) {
        Query query = getDatabase().getReference()
                                   .child(BASE_PATH)
                                   .child(userId)
                                   .orderByChild(FIELD_ORDER);

        return new ObservableDataList<>(query, snapshot -> map(userId, snapshot));
    }

    public void delete(@NonNull String userId, @NonNull String imageId) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(imageId)
                     .removeValue((error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to remove: reference = %s", reference), error.toException());
                         }
                     });
    }

    @NonNull
    private static Value map(@NonNull UploadUserActorImageFileTask uploadUserActorImageFileTask) {
        Value value = new Value();
        value.instanceId = uploadUserActorImageFileTask.instanceId;
        value.sourceUri = uploadUserActorImageFileTask.sourceUriString;
        value.createdAt = ServerTimestampMapper.map(uploadUserActorImageFileTask.createdAt);
        value.order = -System.currentTimeMillis();
        return value;
    }

    @NonNull
    private static UploadUserActorImageFileTask map(@NonNull String userId, @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UploadUserActorImageFileTask uploadUserActorImageFileTask = new UploadUserActorImageFileTask(userId, snapshot.getKey());
        uploadUserActorImageFileTask.instanceId = value.instanceId;
        uploadUserActorImageFileTask.sourceUriString = value.sourceUri;
        uploadUserActorImageFileTask.createdAt = ServerTimestampMapper.map(value.createdAt);
        return uploadUserActorImageFileTask;
    }

    public static final class Value {

        public String instanceId;

        public String sourceUri;

        public Object createdAt;

        // to sort in desc order.
        public long order;
    }
}
