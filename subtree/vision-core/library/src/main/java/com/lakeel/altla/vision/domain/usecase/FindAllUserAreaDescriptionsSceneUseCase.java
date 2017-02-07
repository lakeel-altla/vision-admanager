package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionSceneRepository;
import com.lakeel.altla.vision.domain.model.UserAreaDescriptionScene;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindAllUserAreaDescriptionsSceneUseCase {

    private static final Log LOG = LogFactory.getLog(FindAllUserAreaDescriptionsSceneUseCase.class);

    @Inject
    UserAreaDescriptionSceneRepository userAreaDescriptionSceneRepository;

    @Inject
    public FindAllUserAreaDescriptionsSceneUseCase() {
    }

    @NonNull
    public Observable<UserAreaDescriptionScene> execute(@NonNull String areaDescriptionId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        String userId = user.getUid();

        return Observable.<UserAreaDescriptionScene>create(e -> {
            userAreaDescriptionSceneRepository.findAll(userId, areaDescriptionId, userAreaDescriptionScenes -> {
                for (UserAreaDescriptionScene userAreaDescriptionScene : userAreaDescriptionScenes) {
                    e.onNext(userAreaDescriptionScene);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
