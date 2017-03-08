package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface UserSceneItemView {

    void onUpdateSceneId(@NonNull String sceneId);

    void onUpdateName(@Nullable String name);
}
