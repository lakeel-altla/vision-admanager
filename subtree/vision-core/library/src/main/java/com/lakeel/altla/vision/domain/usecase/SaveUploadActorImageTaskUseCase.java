package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UploadUserActorImageFileTaskRepository;
import com.lakeel.altla.vision.domain.model.UploadUserActorImageFileTask;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUploadActorImageTaskUseCase {

    @Inject
    UploadUserActorImageFileTaskRepository uploadUserActorImageFileTaskRepository;

    @Inject
    public SaveUploadActorImageTaskUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull UploadUserActorImageFileTask uploadUserActorImageFileTask) {
        return Completable.create(e -> {
            uploadUserActorImageFileTaskRepository.save(uploadUserActorImageFileTask);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
