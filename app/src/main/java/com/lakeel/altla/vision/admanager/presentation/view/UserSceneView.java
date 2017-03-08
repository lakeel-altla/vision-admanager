package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserSceneView {

    void onUpdateTitle(@Nullable String name);

    void onUpdateSceneId(@NonNull String sceneId);

    void onUpdateName(@Nullable String name);

    void onUpdateAreaName(@Nullable String areaName);

    void onUpdateCreatedAt(long createdAt);

    void onUpdateUpdatedAt(long updatedAt);

    void onShowUserSceneEditView(@NonNull String sceneId);

    void onSnackbar(@StringRes int resId);
}
