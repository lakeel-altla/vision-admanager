package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface UserAreaEditView {

    void onAreaIdUpdated(String areaId);

    void onCreatedAtUpdated(long createdAt);

    void onModelUpdated(@NonNull UserAreaModel model);

    void onSnackbar(@StringRes int resId);

    void onShowPlacePicker();
}
