package com.lakeel.altla.vision.admanager.presentation.view;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public interface SignInView {

    void showProgressDialog();

    void hideProgressDialog();

    void showGoogleApiClientConnectionFailedSnackbar();

    void showGoogleSignInFailedSnackbar();

    void showGoogleSignInRequiredSnackbar();

    void showSignedOutSnackbar();

    void showTangoPermissionFragment();

    void startGoogleSignInActivity(GoogleSignInOptions options);
}
