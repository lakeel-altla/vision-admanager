package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public interface SignInView {

    // for Google API client
    FragmentActivity getActivity();

    // for Google API client
    Fragment getFragment();

    void showProgressDialog();

    void hideProgressDialog();

    void showGoogleApiClientConnectionFailedSnackbar();

    void showFirebaseSignInFailedSnackbar();

    void showGoogleSignInFailedSnackbar();

    void showGoogleSignInRequiredSnackbar();

    void showSignedOutSnackbar();

    void showTangoPermissionFragment();
}
