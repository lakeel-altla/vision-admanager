package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.SignInView;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public final class SignInPresenter implements GoogleApiClient.OnConnectionFailedListener {

    @Inject
    Resources resources;

    private static final Log LOG = LogFactory.getLog(SignInPresenter.class);

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private final FirebaseAuth.AuthStateListener authStateListener;

    private SignInView view;

    private boolean signedInDetected;

    private ActivityForResult googleSignInActivityForResult;

    @Inject
    public SignInPresenter() {
        // See:
        //
        // http://stackoverflow.com/questions/37674823/firebase-android-onauthstatechanged-fire-twice-after-signinwithemailandpasswor
        //
        authStateListener = firebaseAuth -> {
            SignInView view = this.view;
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
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(view.getActivity())
                .enableAutoManage(view.getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        googleSignInActivityForResult = new ActivityForResult
                .Builder(this.view.getFragment(), Auth.GoogleSignInApi.getSignInIntent(googleApiClient))
                .setListener(this::onGoogleSignInActivityResult)
                .build();
    }

    public void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    public void onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        compositeSubscription.clear();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LOG.e("onConnectionFailed: " + connectionResult);
        view.showGoogleApiClientConnectionFailedSnackbar();
    }

    public void onSignIn() {
        googleSignInActivityForResult.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        googleSignInActivityForResult.onActivityResult(requestCode, resultCode, data);
    }

    private void onGoogleSignInActivityResult(Intent intent, boolean isCanceled) {
        SignInView view = this.view;

        if (!isCanceled) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                LOG.d("Google Sign-In succeeded.");
                LOG.d("Authenticating with Firebase...");

                view.showProgressDialog();

                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null) {
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseAuth
                            .getInstance()
                            .signInWithCredential(authCredential)
                            .addOnCompleteListener(view.getActivity(), task -> {
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
