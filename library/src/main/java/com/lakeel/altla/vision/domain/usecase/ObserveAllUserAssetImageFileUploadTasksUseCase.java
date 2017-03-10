package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetFileUploadTaskRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.ImageAssetFileUploadTask;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveAllUserAssetImageFileUploadTasksUseCase {

    @Inject
    UserImageAssetFileUploadTaskRepository userImageAssetFileUploadTaskRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveAllUserAssetImageFileUploadTasksUseCase() {
    }

    @NonNull
    public Observable<DataListEvent<ImageAssetFileUploadTask>> execute() {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userImageAssetFileUploadTaskRepository.observeAll(userId))
                                 .subscribeOn(Schedulers.io());
    }
}
