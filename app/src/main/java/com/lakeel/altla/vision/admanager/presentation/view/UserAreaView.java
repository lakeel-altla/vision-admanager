package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface UserAreaView {

    void onModelUpdated(@NonNull UserAreaModel model);

    void onShowUserAreaEditView(@NonNull String areaId);

    void onShowUserAreaDescriptionsInAreaView(@NonNull String areaId);

    void onSnackbar(@StringRes int resId);
}
