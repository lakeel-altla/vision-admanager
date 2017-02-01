package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaItemModel;

import android.support.annotation.NonNull;

public interface UserAreaItemView {

    void onModelUpdated(@NonNull UserAreaItemModel model);
}
