package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.SignInView;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public final class SignInPresenter {

    @Inject
    Resources resources;

    @Inject
    FirebaseAuth auth;

    private static final Log LOG = LogFactory.getLog(SignInPresenter.class);

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private final FirebaseAuth.AuthStateListener authStateListener;

    private SignInView view;

    private boolean signedInDetected;

    private GoogleSignInOptions googleSignInOptions;

    @Inject
    public SignInPresenter() {
        // See:
        //
        // http://stackoverflow.com/questions/37674823/firebase-android-onauthstatechanged-fire-twice-after-signinwithemailandpasswor
        //
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                if (!signedInDetected) {
                    LOG.d("signedIn: " + user.getUid());
                    view.showTangoPermissionFragment();
                    signedInDetected = true;
                } else {
                    LOG.d("onAuthStateChanged() fired twice.");
                }
            } else {
                LOG.d("signedOut: ");
                view.showSignedOutSnackbar();
            }
        };
    }

    public void onCreateView(@NonNull SignInView view) {
        this.view = view;

        // Configure Google Sign In
        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    public void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    public void onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        compositeSubscription.clear();
    }

    public void onGoogleApiClientConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LOG.e("onConnectionFailed: " + connectionResult);
        view.showGoogleApiClientConnectionFailedSnackbar();
    }

    public void onSignIn() {
        view.startGoogleSignInActivity(googleSignInOptions);
    }

    public void onSignInResult(boolean isCanceled, GoogleSignInResult result) {
        if (!isCanceled) {
            if (result.isSuccess()) {
                LOG.d("Google Sign-In succeeded.");
                LOG.d("Authenticating with Firebase...");

                view.showProgressDialog();

                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null) {
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    auth.signInWithCredential(authCredential)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                LOG.e("Firebase Sign-In failed.", task.getException());
                            }
                            view.hideProgressDialog();
                        });
                } else {
                    LOG.e("GoogleSignInAccount is null.");
                    view.showGoogleSignInFailedSnackbar();
                }
            } else {
                LOG.d("Google Sign-In failed.");
                view.showGoogleSignInFailedSnackbar();
            }
        } else {
            LOG.d("Google Sign-In canceled.");
            view.showGoogleSignInRequiredSnackbar();
        }
    }
}
