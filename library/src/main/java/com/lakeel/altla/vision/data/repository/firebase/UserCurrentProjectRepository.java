package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.mapper.ServerTimestampMapper;
import com.lakeel.altla.vision.domain.model.UserCurrentProject;

import android.support.annotation.NonNull;

public final class UserCurrentProjectRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserCurrentProjectRepository.class);

    private static final String BASE_PATH = "userCurrentProjects";

    public UserCurrentProjectRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull UserCurrentProject userCurrentProject) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userCurrentProject.userId)
                     .child(userCurrentProject.instanceId)
                     .setValue(map(userCurrentProject), (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String instanceId,
                     OnSuccessListener<UserCurrentProject> onSuccessListener, OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(instanceId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserCurrentProject userCurrentProject = null;
                             if (snapshot.exists()) {
                                 userCurrentProject = map(userId, snapshot);
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userCurrentProject);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    @NonNull
    private static Value map(@NonNull UserCurrentProject userCurrentProject) {
        Value value = new Value();
        value.areaId = userCurrentProject.areaId;
        value.areaDescriptionId = userCurrentProject.areaDescriptionId;
        value.sceneId = userCurrentProject.sceneId;
        value.areaId = userCurrentProject.areaId;
        value.createdAt = ServerTimestampMapper.map(userCurrentProject.createdAt);
        value.updatedAt = ServerValue.TIMESTAMP;
        return value;
    }

    @NonNull
    private static UserCurrentProject map(@NonNull String userId, @NonNull DataSnapshot snapshot) {
        Value value = snapshot.getValue(Value.class);
        UserCurrentProject userCurrentProject = new UserCurrentProject(userId, snapshot.getKey());
        userCurrentProject.areaId = value.areaId;
        userCurrentProject.areaDescriptionId = value.areaDescriptionId;
        userCurrentProject.sceneId = value.sceneId;
        userCurrentProject.areaId = value.areaId;
        userCurrentProject.createdAt = ServerTimestampMapper.map(value.createdAt);
        userCurrentProject.updatedAt = ServerTimestampMapper.map(value.updatedAt);
        return userCurrentProject;
    }

    public static final class Value {

        public String areaId;

        public String areaDescriptionId;

        public String sceneId;

        public Object createdAt;

        public Object updatedAt;
    }
}
