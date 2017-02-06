package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionEditModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface UserAreaDescriptionEditView {

    void onUpdateTitle(@NonNull String title);

    void onAreaNameUpdated(String areaName);

    void onModelUpdated(UserAreaDescriptionEditModel model);

    void onSnackbar(@StringRes int resId);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onShowUserAreaSelectView();
}
