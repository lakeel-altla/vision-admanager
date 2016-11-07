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

    private TangoPermissionView mView;

    private ActivityForResult mActivityForResult;

    @Inject
    public TangoPermissionPresenter() {
    }

    public void onCreateView(@NonNull TangoPermissionView view) {
        mView = view;

        mActivityForResult = new ActivityForResult
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
        mActivityForResult.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mActivityForResult.onActivityResult(requestCode, resultCode, data);
    }
}
