package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageFileUploadTaskRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.UserAssetImageFileUploadTask;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveAllUserAssetImageFileUploadTasksUseCase {

    @Inject
    UserAssetImageFileUploadTaskRepository userAssetImageFileUploadTaskRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveAllUserAssetImageFileUploadTasksUseCase() {
    }

    @NonNull
    public Observable<DataListEvent<UserAssetImageFileUploadTask>> execute() {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> userAssetImageFileUploadTaskRepository.observeAll(userId))
                                 .subscribeOn(Schedulers.io());
    }
}
