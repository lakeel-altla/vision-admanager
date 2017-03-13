package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.data.repository.firebase.UserCurrentAreaSettingsRepository;
import com.lakeel.altla.vision.domain.model.AreaSettings;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class SaveUserCurrentAreaSettingsUseCase {

    @Inject
    UserCurrentAreaSettingsRepository userCurrentAreaSettingsRepository;

    @Inject
    public SaveUserCurrentAreaSettingsUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull AreaSettings settings) {
        return Completable.create(e -> {
            userCurrentAreaSettingsRepository.save(settings);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
