package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface UserAreaDescriptionView {

    void onModelUpdated(@NonNull UserAreaDescriptionModel model);

    void onSnackbar(@StringRes int resId);
}
