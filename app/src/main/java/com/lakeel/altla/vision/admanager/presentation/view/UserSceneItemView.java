package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserSceneItemModel;

import android.support.annotation.NonNull;

public interface UserSceneItemView {

    void onModelUpdated(@NonNull UserSceneItemModel model);
}
