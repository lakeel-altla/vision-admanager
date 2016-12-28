package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaDescriptionModel;

import android.support.annotation.StringRes;

public interface EditUserAreaDescriptionView {

    void showModel(EditUserAreaDescriptionModel model);

    void showSnackbar(@StringRes int resId);

    void showNameError(@StringRes int resId);

    void hideNameError();

    void showPlacePicker();
}
