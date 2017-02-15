package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserSceneEditView {

    void onUpdateViewsEnabled(boolean enabled);

    void onUpdateButtonSaveEnabled(boolean enabled);

    void onUpdateTitle(@Nullable String title);

    void onUpdateName(@Nullable String name);

    void onUpdateAreaName(@Nullable String areaName);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onShowUserAreaSelectView();

    void onSnackbar(@StringRes int resId);
}
