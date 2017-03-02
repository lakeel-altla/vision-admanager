package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserCurrentProjectRepository;
import com.lakeel.altla.vision.domain.model.UserCurrentProject;

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
    public Completable execute(@NonNull UserCurrentProject userCurrentProject) {
        return Completable.create(e -> {
            userCurrentProjectRepository.save(userCurrentProject);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
