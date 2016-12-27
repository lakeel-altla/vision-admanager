package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.presentation.view.TangoPermissionView;

import android.support.annotation.NonNull;

import javax.inject.Inject;

public final class TangoPermissionPresenter {

    private static final Log LOG = LogFactory.getLog(TangoPermissionPresenter.class);

    private TangoPermissionView view;

    @Inject
    public TangoPermissionPresenter() {
    }

    public void onCreateView(@NonNull TangoPermissionView view) {
        this.view = view;

        onConfirmPermission();
    }

    public void onConfirmPermission() {
        view.startTangoPermissionActivity();
    }

    public void onTangoPermissionResult(boolean isCanceled) {
        if (!isCanceled) {
            LOG.d("Tango permission granted.");
            view.closeTangoPermissionFragment();
        } else {
            LOG.d("Tango permission not granted.");
            view.showAreaLearningPermissionRequiredSnackbar();
        }
    }
}
