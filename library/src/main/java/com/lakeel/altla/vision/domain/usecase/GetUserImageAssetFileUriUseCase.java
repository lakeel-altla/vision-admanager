package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetFileRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;

import android.net.Uri;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class GetUserImageAssetFileUriUseCase {

    @Inject
    UserImageAssetFileRepository userImageAssetFileRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public GetUserImageAssetFileUriUseCase() {
    }

    @NonNull
    public Single<Uri> execute(@NonNull String assetId) {
        String userId = currentUserResolver.getUserId();

        return Single.<Uri>create(e -> {
            userImageAssetFileRepository.getDownloadUri(userId, assetId, e::onSuccess, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
