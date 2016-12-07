package com.lakeel.altla.vision.admanager.presentation.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface SignInView {

    void showProgressDialog();

    void hideProgressDialog();

    void showSnackbar(@StringRes int resId);

    void showTangoPermissionFragment();

    void startActivityForResult(@NonNull Intent intent, int requestCode);
}
