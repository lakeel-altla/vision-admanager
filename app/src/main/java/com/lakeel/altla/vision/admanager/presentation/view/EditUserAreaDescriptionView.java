package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaDescriptionModel;

import android.support.annotation.StringRes;

public interface EditUserAreaDescriptionView {

    void onAreaNameUpdated(String areaName);

    void onModelUpdated(EditUserAreaDescriptionModel model);

    void onSnackbar(@StringRes int resId);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onShowSelectUserAreaView();
}
