package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserCurrentAreaSettingsRepository;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.AreaSettings;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public final class FindUserCurrentAreaSettingsUseCase {

    @Inject
    UserCurrentAreaSettingsRepository userCurrentAreaSettingsRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    CurrentDeviceResolver currentDeviceResolver;

    @Inject
    public FindUserCurrentAreaSettingsUseCase() {
    }

    @NonNull
    public Maybe<AreaSettings> execute() {
        String userId = currentUserResolver.getUserId();
        String instanceId = currentDeviceResolver.getInstanceId();

        return Maybe.<AreaSettings>create(e -> {
            userCurrentAreaSettingsRepository.find(userId, instanceId, userCurrentProject -> {
                if (userCurrentProject == null) {
                    e.onComplete();
                } else {
                    e.onSuccess(userCurrentProject);
                }
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
