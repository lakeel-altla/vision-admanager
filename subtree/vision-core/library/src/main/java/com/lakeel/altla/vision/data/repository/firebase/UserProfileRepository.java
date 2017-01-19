package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.rx.firebase.database.RxFirebaseQuery;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserProfile;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;

public final class UserProfileRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserProfileRepository.class);

    private static final String PATH_USER_PROFILES = "userProfiles";

    public UserProfileRepository(FirebaseDatabase database) {
        super(database);
    }

    public Completable save(UserProfile userProfile) {
        if (userProfile == null) throw new ArgumentNullException("userProfile");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                getRootReference().child(PATH_USER_PROFILES)
                                  .child(userProfile.userId)
                                  .setValue(userProfile, (error, reference) -> {
                                      if (error != null) {
                                          LOG.e("Failed to save.", error.toException());
                                      }
                                  });

                subscriber.onCompleted();
            }
        });
    }

    public Observable<UserProfile> find(String userId) {
        if (userId == null) throw new ArgumentNullException("userId");

        return Observable.<DatabaseReference>create(subscriber -> {
            DatabaseReference reference = getRootReference().child(PATH_USER_PROFILES)
                                                            .child(userId);

            subscriber.onNext(reference);
            subscriber.onCompleted();
        }).flatMap(RxFirebaseQuery::asObservableForSingleValueEvent)
          .filter(DataSnapshot::exists)
          .map(snapshot -> map(userId, snapshot));
    }

    public Observable<UserProfile> observe(String userId) {
        if (userId == null) throw new ArgumentNullException("userId");

        return Observable.<DatabaseReference>create(subscriber -> {
            DatabaseReference reference = getRootReference().child(PATH_USER_PROFILES)
                                                            .child(userId);

            subscriber.onNext(reference);
        }).flatMap(RxFirebaseQuery::asObservable)
          .filter(DataSnapshot::exists)
          .map(snapshot -> map(userId, snapshot));
    }

    private UserProfile map(String userId, DataSnapshot snapshot) {
        UserProfile userProfile = snapshot.getValue(UserProfile.class);
        userProfile.userId = userId;
        return userProfile;
    }
}
