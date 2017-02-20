package com.lakeel.altla.vision.admanager.presentation.view;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserAreaEditView {

    void onUpdateViewsEnabled(boolean enabled);

    void onUpdateActionSave(boolean enabled);

    void onUpdateHomeAsUpIndicator(@DrawableRes int resId);

    void onUpdateHomeAsUpIndicator(@Nullable Drawable drawable);

    void onUpdateButtonRemovePlaceEnabled(boolean enabled);

    void onUpdateTitle(@Nullable String title);

    void onUpdateName(@Nullable String name);

    void onUpdatePlaceName(@Nullable String placeName);

    void onUpdatePlaceAddress(@Nullable String placeAddress);

    void onUpdateLevel(int level);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onShowPlacePicker();

    void onBackView();

    void onSnackbar(@StringRes int resId);
}
