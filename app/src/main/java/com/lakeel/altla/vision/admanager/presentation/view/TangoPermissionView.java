package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.v4.app.Fragment;

public interface TangoPermissionView {

    // for Tango Permission Activity
    Fragment getFragment();

    void startManagerActivity();

    void showAreaLearningPermissionRequiredSnackbar();
}
