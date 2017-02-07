package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.presentation.view.TangoPermissionView;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import javax.inject.Inject;

public final class TangoPermissionPresenter extends BasePresenter<TangoPermissionView> {

    @Inject
    public TangoPermissionPresenter() {
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        onConfirmPermission();
    }

    public void onConfirmPermission() {
        getView().onShowTangoPermissionActivity();
    }

    public void onTangoPermissionResult(boolean isCanceled) {
        if (!isCanceled) {
            getLog().d("Tango permission granted.");
            getView().onCloseTangoPermissionView();
        } else {
            getLog().d("Tango permission not granted.");
            getView().onShowAreaLearningPermissionRequiredSnackbar();
        }
    }
}
