package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.vision.helper.OnFailureListener;
import com.lakeel.altla.vision.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.model.AreaSettings;

import android.support.annotation.NonNull;

public final class UserCurrentAreaSettingsRepository extends BaseDatabaseRepository {

    private static final String BASE_PATH = "userCurrentAreaSettings";

    public UserCurrentAreaSettingsRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull AreaSettings areaSettings) {
        if (areaSettings.getUserId() == null) {
            throw new IllegalArgumentException("areaSettings.getUserId() must be not null.");
        }

        areaSettings.setUpdatedAtAsLong(-1);

        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(areaSettings.getUserId())
                     .child(areaSettings.getId())
                     .setValue(areaSettings, (error, reference) -> {
                         if (error != null) {
                             getLog().e(String.format("Failed to save: reference = %s", reference),
                                        error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String instanceId,
                     OnSuccessListener<AreaSettings> onSuccessListener, OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(instanceId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             AreaSettings areaSettings = snapshot.getValue(AreaSettings.class);
                             if (onSuccessListener != null) onSuccessListener.onSuccess(areaSettings);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }
}
