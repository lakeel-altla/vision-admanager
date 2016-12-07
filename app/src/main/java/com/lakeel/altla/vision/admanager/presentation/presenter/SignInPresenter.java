package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.presentation.view.SignInView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public final class SignInPresenter implements OnConnectionFailedListener {

    private static final Log LOG = LogFactory.getLog(SignInPresenter.class);

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 0;

    @Inject
    Resources resources;

    @Inject
    GoogleSignInOptions googleSignInOptions;

    @Inject
    AppCompatActivity activity;

    @Inject
    FirebaseAuth auth;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private final FirebaseAuth.AuthStateListener authStateListener;

    private SignInView view;

    private GoogleApiClient googleApiClient;

    private boolean signedInDetected;

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

        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    public void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    public void onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        compositeSubscription.clear();
    }

    public void onClickButtonSignIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);

        view.startActivityForResult(intent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE_GOOGLE_SIGN_IN) {
            // Ignore.
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LOG.e("onConnectionFailed: " + connectionResult);
        view.showGoogleApiClientConnectionFailedSnackbar();
    }
}
