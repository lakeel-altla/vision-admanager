package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UploadUserActorImageFileTaskRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.UploadUserActorImageFileTask;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class ObserveAllUploadActorImageFileTasksUseCase {

    @Inject
    UploadUserActorImageFileTaskRepository uploadUserActorImageFileTaskRepository;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    public ObserveAllUploadActorImageFileTasksUseCase() {
    }

    @NonNull
    public Observable<DataListEvent<UploadUserActorImageFileTask>> execute() {
        String userId = currentUserResolver.getUserId();

        return ObservableDataList.using(() -> uploadUserActorImageFileTaskRepository.observeAll(userId))
                                 .subscribeOn(Schedulers.io());
    }
}
