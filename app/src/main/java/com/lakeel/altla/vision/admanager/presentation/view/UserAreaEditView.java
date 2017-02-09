package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserAreaEditView {

    void onModelUpdated(@NonNull UserAreaModel model);

    void onUpdateTitle(@Nullable String title);

    void onShowPlacePicker();

    void onSnackbar(@StringRes int resId);
}
