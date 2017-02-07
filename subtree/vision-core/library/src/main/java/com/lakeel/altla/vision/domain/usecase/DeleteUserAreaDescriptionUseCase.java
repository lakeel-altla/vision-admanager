package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DeleteUserAreaDescriptionUseCase {

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public DeleteUserAreaDescriptionUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull String areaDescriptionId) {
        String userId = currentUserResolver.getUserId();

        return Completable.create(e -> userAreaDescriptionFileRepository.delete(userId, areaDescriptionId, aVoid -> {
            userAreaDescriptionRepository.delete(userId, areaDescriptionId);
            e.onComplete();
        }, e::onError)).subscribeOn(Schedulers.io());
    }
}
