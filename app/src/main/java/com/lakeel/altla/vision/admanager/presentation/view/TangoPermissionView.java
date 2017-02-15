package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.StringRes;

public interface TangoPermissionView {

    void onUpdateTitle(@StringRes int resId);

    void onCloseTangoPermissionView();

    void onShowAreaLearningPermissionRequiredSnackbar();

    void onShowTangoPermissionActivity();
}
