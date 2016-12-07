package com.lakeel.altla.vision.admanager.presentation.view;

import android.content.Intent;
import android.support.annotation.NonNull;

public interface SignInView {

    void showProgressDialog();

    void hideProgressDialog();

    void showGoogleApiClientConnectionFailedSnackbar();

    void showGoogleSignInFailedSnackbar();

    void showGoogleSignInRequiredSnackbar();

    void showSignedOutSnackbar();

    void showTangoPermissionFragment();

    void startActivityForResult(@NonNull Intent intent, int requestCode);
}
