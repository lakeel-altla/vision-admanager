package com.lakeel.altla.vision.api;

import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.data.repository.firebase.UserCurrentAreaSettingsRepository;
import com.lakeel.altla.vision.helper.OnFailureListener;
import com.lakeel.altla.vision.helper.OnSuccessListener;
import com.lakeel.altla.vision.model.AreaSettings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class UserAreaSettingsApi extends BaseVisionApi {

    private final UserCurrentAreaSettingsRepository userCurrentAreaSettingsRepository;

    public UserAreaSettingsApi(@NonNull VisionService visionService) {
        super(visionService);

        userCurrentAreaSettingsRepository = new UserCurrentAreaSettingsRepository(visionService.getFirebaseDatabase());
    }

    public void findUserCurrentAreaSettings(@Nullable OnSuccessListener<AreaSettings> onSuccessListener,
                                            @Nullable OnFailureListener onFailureListener) {
        userCurrentAreaSettingsRepository.find(CurrentUser.getInstance().getUserId(),
                                               FirebaseInstanceId.getInstance().getId(),
                                               onSuccessListener, onFailureListener);
    }

    public void saveUserCurrentAreaSettings(@NonNull AreaSettings areaSettings) {
        if (!CurrentUser.getInstance().getUserId().equals(areaSettings.getUserId())) {
            throw new IllegalArgumentException("Invalid user id.");
        }

        userCurrentAreaSettingsRepository.save(areaSettings);
    }
}
