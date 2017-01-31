package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface EditUserAreaView {

    void showAreaId(String areaId);

    void showCreatedAt(long createdAt);

    void showModel(@NonNull EditUserAreaModel model);

    void showSnackbar(@StringRes int resId);

    void showPlacePicker();
}
