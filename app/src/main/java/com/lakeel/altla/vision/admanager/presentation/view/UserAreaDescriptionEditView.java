package com.lakeel.altla.vision.admanager.presentation.view;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserAreaDescriptionEditView {

    void onUpdateViewsEnabled(boolean enabled);

    void onUpdateActionSave(boolean enabled);

    void onUpdateHomeAsUpIndicator(@DrawableRes int resId);

    void onUpdateHomeAsUpIndicator(@Nullable Drawable drawable);

    void onUpdateTitle(@Nullable String title);

    void onUpdateName(String name);

    void onUpdateAreaName(String areaName);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onShowUserAreaSelectView();

    void onBackView();

    void onSnackbar(@StringRes int resId);
}
