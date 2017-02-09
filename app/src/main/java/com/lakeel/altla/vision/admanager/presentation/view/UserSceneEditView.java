package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserSceneModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserSceneEditView {

    void onModelUpdated(@NonNull UserSceneModel model);

    void onUpdateTitle(@Nullable String title);

    void onSnackbar(@StringRes int resId);
}
