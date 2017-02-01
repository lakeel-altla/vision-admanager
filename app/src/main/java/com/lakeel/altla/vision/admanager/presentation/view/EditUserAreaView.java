package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface EditUserAreaView {

    void onAreaIdUpdated(String areaId);

    void onCreatedAtUpdated(long createdAt);

    void onModelUpdated(@NonNull EditUserAreaModel model);

    void onSnackbar(@StringRes int resId);

    void onShowPlacePicker();
}
