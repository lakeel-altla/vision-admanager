package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserActorImageFileRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;

import android.net.Uri;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class GetUserActorImageFileUriUseCase {

    @Inject
    UserActorImageFileRepository userActorImageFileRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public GetUserActorImageFileUriUseCase() {
    }

    @NonNull
    public Single<Uri> execute(@NonNull String imageId) {
        String userId = currentUserResolver.getUserId();

        return Single.<Uri>create(e -> {
            userActorImageFileRepository.getDownloadUri(userId, imageId, e::onSuccess, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
