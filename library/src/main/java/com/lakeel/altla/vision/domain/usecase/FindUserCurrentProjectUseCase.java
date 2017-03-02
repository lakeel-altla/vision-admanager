package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserCurrentProjectRepository;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserCurrentProject;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public final class FindUserCurrentProjectUseCase {

    @Inject
    UserCurrentProjectRepository userCurrentProjectRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    CurrentDeviceResolver currentDeviceResolver;

    @Inject
    public FindUserCurrentProjectUseCase() {
    }

    @NonNull
    public Maybe<UserCurrentProject> execute() {
        String userId = currentUserResolver.getUserId();
        String instanceId = currentDeviceResolver.getInstanceId();

        return Maybe.<UserCurrentProject>create(e -> {
            userCurrentProjectRepository.find(userId, instanceId, userCurrentProject -> {
                if (userCurrentProject == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(userCurrentProject);
                }
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
