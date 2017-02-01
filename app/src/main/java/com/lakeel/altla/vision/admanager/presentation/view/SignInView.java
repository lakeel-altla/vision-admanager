package com.lakeel.altla.vision.admanager.presentation.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface SignInView {

    void onShowProgressDialog();

    void onHideProgressDialog();

    void onSnackbar(@StringRes int resId);

    void onCloseSignInView();

    void onStartActivityForResult(@NonNull Intent intent, int requestCode);
}
