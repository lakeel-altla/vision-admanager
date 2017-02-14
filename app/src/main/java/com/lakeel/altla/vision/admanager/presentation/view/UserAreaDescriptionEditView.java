package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserAreaDescriptionEditView {

    void onUpdateViewsEnabled(boolean enabled);

    void onUpdateTitle(@Nullable String title);

    void onUpdateName(String name);

    void onUpdateAreaName(String areaName);

    void onSnackbar(@StringRes int resId);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onShowUserAreaSelectView();
}
