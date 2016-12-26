package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.rx.firebase.database.RxFirebaseQuery;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserTexture;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;

public final class UserTextureRepositoryImpl implements UserTextureRepository {

    private static final Log LOG = LogFactory.getLog(UserTextureRepositoryImpl.class);

    private static final String PATH_USER_TEXTURES = "userTextures";

    private final DatabaseReference rootReference;

    public UserTextureRepositoryImpl(DatabaseReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    @Override
    public Completable save(UserTexture userTexture) {
        if (userTexture == null) throw new ArgumentNullException("userTexture");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                UserTextureValue value = new UserTextureValue();
                value.name = userTexture.name;

                rootReference.child(PATH_USER_TEXTURES)
                             .child(userTexture.userId)
                             .child(userTexture.textureId)
                             .setValue(value, (error, reference) -> {
                                 if (error != null) {
                                     LOG.e("Failed to save.", error.toException());
                                 }
                             });

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<UserTexture> find(String userId, String textureId) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (textureId == null) throw new ArgumentNullException("textureId");

        return Observable.<DatabaseReference>create(subscriber -> {
            DatabaseReference reference = rootReference.child(PATH_USER_TEXTURES)
                                                       .child(userId)
                                                       .child(textureId);
            subscriber.onNext(reference);
            subscriber.onCompleted();
        }).flatMap(RxFirebaseQuery::asObservableForSingleValueEvent)
          .filter(DataSnapshot::exists)
          .map(snapshot -> map(userId, snapshot));
    }

    @Override
    public Observable<UserTexture> findAll(String userId) {
        if (userId == null) throw new ArgumentNullException("userId");

        return Observable.<Query>create(subscriber -> {
            Query query = rootReference.child(PATH_USER_TEXTURES)
                                       .child(userId)
                                       .orderByChild(UserTextureValue.FIELD_NAME);
            subscriber.onNext(query);
            subscriber.onCompleted();
        }).flatMap(RxFirebaseQuery::asObservableForSingleValueEvent)
          .flatMap(snapshot -> Observable.from(snapshot.getChildren()))
          .map(snapshot -> map(userId, snapshot));
    }

    @Override
    public Completable delete(String userId, String textureId) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (textureId == null) throw new ArgumentNullException("textureId");

        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                rootReference.child(PATH_USER_TEXTURES)
                             .child(userId)
                             .child(textureId)
                             .removeValue((error, reference) -> {
                                 if (error != null) {
                                     LOG.e("Failed to delete.", error.toException());
                                 }
                             });

                subscriber.onCompleted();
            }
        });
    }

    private UserTexture map(String userId, DataSnapshot snapshot) {
        String textureId = snapshot.getKey();
        UserTextureValue value = snapshot.getValue(UserTextureValue.class);

        return new UserTexture(userId, textureId, value.name);
    }

    public static final class UserTextureValue {

        private static final String FIELD_NAME = "name";

        public String name;
    }
}
