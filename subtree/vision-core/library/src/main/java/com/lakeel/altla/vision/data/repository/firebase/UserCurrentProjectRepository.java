package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.model.CurrentProject;

import android.support.annotation.NonNull;

public final class UserCurrentProjectRepository extends BaseDatabaseRepository {

    private static final String BASE_PATH = "userCurrentProjects";

    public UserCurrentProjectRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull CurrentProject currentProject) {
        if (currentProject.getUserId() == null) {
            throw new IllegalArgumentException("currentProject.getUserId() must be not null.");
        }

        currentProject.setUpdatedAtAsLong(-1);

        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(currentProject.getUserId())
                     .child(currentProject.getId())
                     .setValue(currentProject, (error, reference) -> {
                         if (error != null) {
                             getLog().e(String.format("Failed to save: reference = %s", reference),
                                        error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String instanceId,
                     OnSuccessListener<CurrentProject> onSuccessListener, OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(instanceId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             CurrentProject currentProject = snapshot.getValue(CurrentProject.class);
                             if (onSuccessListener != null) onSuccessListener.onSuccess(currentProject);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }
}
