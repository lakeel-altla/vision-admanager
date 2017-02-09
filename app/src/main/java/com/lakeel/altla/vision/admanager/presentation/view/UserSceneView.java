package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserSceneModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface UserSceneView {

    void onModelUpdated(@NonNull UserSceneModel model);

    void onShowUserSceneEditView(@NonNull String sceneId);

    void onSnackbar(@StringRes int resId);
}
