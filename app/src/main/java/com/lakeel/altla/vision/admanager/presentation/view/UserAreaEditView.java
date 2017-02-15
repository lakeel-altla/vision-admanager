package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserAreaEditView {

    void onUpdateViewsEnabled(boolean enabled);

    void onUpdateButtonRemovePlaceEnabled(boolean enabled);

    void onUpdateButtonSaveEnabled(boolean enabled);

    void onUpdateTitle(@Nullable String title);

    void onUpdateFields(@NonNull UserAreaModel model);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onShowPlacePicker();

    void onSnackbar(@StringRes int resId);
}
