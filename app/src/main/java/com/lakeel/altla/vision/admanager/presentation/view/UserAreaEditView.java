package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserAreaEditView {

    void onUpdateViewsEnabled(boolean enabled);

    void onUpdateButtonRemovePlaceEnabled(boolean enabled);

    void onUpdateButtonSaveEnabled(boolean enabled);

    void onUpdateTitle(@Nullable String title);

    void onUpdateName(@Nullable String name);

    void onUpdatePlaceName(@Nullable String placeName);

    void onUpdatePlaceAddress(@Nullable String placeAddress);

    void onUpdateLevel(int level);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onShowPlacePicker();

    void onSnackbar(@StringRes int resId);
}
