package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.data.repository.firebase.UserSceneRepository;
import com.lakeel.altla.vision.domain.model.UserScene;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindAllUserScenesUseCase {

    private static final Log LOG = LogFactory.getLog(FindAllUserScenesUseCase.class);

    @Inject
    UserSceneRepository userSceneRepository;

    @Inject
    public FindAllUserScenesUseCase() {
    }

    @NonNull
    public Observable<UserScene> execute(@NonNull String areaDescriptionId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        String userId = user.getUid();

        return Observable.<UserScene>create(e -> {
            userSceneRepository.findAll(userId, userScenes -> {
                for (UserScene userScene : userScenes) {
                    e.onNext(userScene);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
