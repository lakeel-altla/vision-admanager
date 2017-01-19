package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.rx.firebase.database.RxFirebaseQuery;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;

public final class UserAreaDescriptionRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserAreaDescriptionRepository.class);

    private static final String PATH_USER_AREA_DESCRIPTIONS = "userAreaDescriptions";

    public UserAreaDescriptionRepository(FirebaseDatabase database) {
        super(database);
    }

    public Completable save(UserAreaDescription userAreaDescription) {
        if (userAreaDescription == null) throw new ArgumentNullException("userAreaDescription");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                getRootReference().child(PATH_USER_AREA_DESCRIPTIONS)
                                  .child(userAreaDescription.userId)
                                  .child(userAreaDescription.areaDescriptionId)
                                  .setValue(userAreaDescription, (error, reference) -> {
                                      if (error != null) {
                                          LOG.e(String.format("Failed to save: reference = %s", reference),
                                                error.toException());
                                      }
                                  });

                subscriber.onCompleted();
            }
        });
    }

    public Observable<UserAreaDescription> find(String userId, String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Observable.<DatabaseReference>create(subscriber -> {
            DatabaseReference reference = getRootReference().child(PATH_USER_AREA_DESCRIPTIONS)
                                                            .child(userId)
                                                            .child(areaDescriptionId);

            subscriber.onNext(reference);
            subscriber.onCompleted();
        }).flatMap(RxFirebaseQuery::asObservableForSingleValueEvent)
          .filter(DataSnapshot::exists)
          .map(snapshot -> map(userId, snapshot));
    }

    public Observable<UserAreaDescription> findAll(String userId) {
        return Observable.<Query>create(subscriber -> {
            Query query = getRootReference().child(PATH_USER_AREA_DESCRIPTIONS)
                                            .child(userId)
                                            .orderByValue();

            subscriber.onNext(query);
            subscriber.onCompleted();
        }).flatMap(RxFirebaseQuery::asObservableForSingleValueEvent)
          .flatMap(snapshot -> Observable.from(snapshot.getChildren()))
          .map(snapshot -> map(userId, snapshot));
    }

    public Completable delete(String userId, String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                getRootReference().child(PATH_USER_AREA_DESCRIPTIONS)
                                  .child(userId)
                                  .child(areaDescriptionId)
                                  .removeValue((error, reference) -> {
                                      if (error != null) {
                                          LOG.e(String.format("Failed to remove: reference = %s", reference),
                                                error.toException());
                                      }
                                  });

                subscriber.onCompleted();
            }
        });
    }

    private UserAreaDescription map(String userId, DataSnapshot snapshot) {
        UserAreaDescription userAreaDescription = snapshot.getValue(UserAreaDescription.class);
        userAreaDescription.userId = userId;
        userAreaDescription.areaDescriptionId = snapshot.getKey();
        return userAreaDescription;
    }
}
