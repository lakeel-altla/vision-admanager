package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindAllUserAreasUseCase {

    private static final Log LOG = LogFactory.getLog(FindAllUserAreasUseCase.class);

    @Inject
    UserAreaRepository userAreaRepository;

    @Inject
    public FindAllUserAreasUseCase() {
    }

    @NonNull
    public Observable<UserArea> execute() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        String userId = user.getUid();

        return Observable.<UserArea>create(e -> {
            userAreaRepository.findAll(userId, userAreas -> {
                for (UserArea userArea : userAreas) {
                    e.onNext(userArea);
                }
                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
