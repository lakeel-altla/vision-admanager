package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.model.Actor;

import android.support.annotation.NonNull;

public final class UserActorRepository extends BaseDatabaseRepository {

    private static final String BASE_PATH = "userActors";

    public UserActorRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull Actor actor) {
        if (actor.getUserId() == null) throw new IllegalArgumentException("actor.getUserId() must be not null.");
        if (actor.getSceneId() == null) throw new IllegalArgumentException("actor.getSceneId() must be not null.");

        actor.setUpdatedAtAsLong(-1);

        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(actor.getUserId())
                     .child(actor.getSceneId())
                     .child(actor.getId())
                     .setValue(actor, (error, reference) -> {
                         if (error != null) {
                             getLog().e(String.format("Failed to save: reference = %s", reference),
                                        error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String sceneId, @NonNull String actorId,
                     OnSuccessListener<Actor> onSuccessListener, OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(sceneId)
                     .child(actorId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             Actor actor = snapshot.getValue(Actor.class);
                             if (onSuccessListener != null) onSuccessListener.onSuccess(actor);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    @NonNull
    public ObservableData<Actor> observe(@NonNull String userId, @NonNull String sceneId, @NonNull String actorId) {
        DatabaseReference reference = getDatabase().getReference()
                                                   .child(BASE_PATH)
                                                   .child(userId)
                                                   .child(sceneId)
                                                   .child(actorId);

        return new ObservableData<>(reference, snapshot -> snapshot.getValue(Actor.class));
    }

    @NonNull
    public ObservableDataList<Actor> observeAll(@NonNull String userId, @NonNull String sceneId) {
        Query query = getDatabase().getReference()
                                   .child(BASE_PATH)
                                   .child(userId)
                                   .child(sceneId);

        return new ObservableDataList<>(query, snapshot -> snapshot.getValue(Actor.class));
    }

    public void delete(@NonNull String userId, @NonNull String sceneId, @NonNull String actorId) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(sceneId)
                     .child(actorId)
                     .removeValue((error, reference) -> {
                         if (error != null) {
                             getLog().e(String.format("Failed to remove: reference = %s", reference),
                                        error.toException());
                         }
                     });
    }
}
