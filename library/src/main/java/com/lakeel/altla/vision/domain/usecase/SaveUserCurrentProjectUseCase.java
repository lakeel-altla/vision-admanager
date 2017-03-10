package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserCurrentProjectRepository;
import com.lakeel.altla.vision.domain.model.CurrentProject;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserCurrentProjectUseCase {

    @Inject
    UserCurrentProjectRepository userCurrentProjectRepository;

    @Inject
    public SaveUserCurrentProjectUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull CurrentProject currentProject) {
        return Completable.create(e -> {
            userCurrentProjectRepository.save(currentProject);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
