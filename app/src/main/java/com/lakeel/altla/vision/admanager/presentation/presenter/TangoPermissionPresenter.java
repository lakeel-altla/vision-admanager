package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoPermissions;
import com.lakeel.altla.vision.admanager.presentation.view.TangoPermissionView;

import android.content.Intent;
import android.support.annotation.NonNull;

import javax.inject.Inject;

public final class TangoPermissionPresenter {

    private static final Log LOGGER = LogFactory.getLog(TangoPermissionPresenter.class);

    private TangoPermissionView view;

    private ActivityForResult activityForResult;

    @Inject
    public TangoPermissionPresenter() {
    }

    public void onCreateView(@NonNull TangoPermissionView view) {
        this.view = view;

        activityForResult = new ActivityForResult
                .Builder(view.getFragment(), TangoPermissions.getRequestAdfLoadSavePermissionIntent())
                .setListener((intent, isCanceled) -> {
                    if (!isCanceled) {
                        LOGGER.d("Tango permission granted.");
                        view.startManagerActivity();
                    } else {
                        LOGGER.d("Tango permission not granted.");
                        view.showAreaLearningPermissionRequiredSnackbar();
                    }
                })
                .build();
    }

    public void onConfirmPermission() {
        activityForResult.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityForResult.onActivityResult(requestCode, resultCode, data);
    }
}
