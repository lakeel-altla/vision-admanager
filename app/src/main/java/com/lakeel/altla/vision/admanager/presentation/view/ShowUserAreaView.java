package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface ShowUserAreaView {

    void onModelUpdated(@NonNull UserAreaModel model);

    void onEdit(@NonNull String areaId);

    void onShowUserAreaDescriptionsInArea(@NonNull String areaId);

    void onSnackbar(@StringRes int resId);
}
