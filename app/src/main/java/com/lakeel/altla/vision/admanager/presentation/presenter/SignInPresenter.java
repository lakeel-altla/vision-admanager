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

    // MEMO:
    //
    // Google Sign-In や Firebase Sign-In は Activity に強く依存しているため、
    // あえて UseCase を作らずに Presenter 内部で処理している。
    //

    @Inject
    Resources mResources;

    private static final Log LOG = LogFactory.getLog(SignInPresenter.class);

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    private final FirebaseAuth.AuthStateListener mAuthListener;

    private SignInView mView;

    private boolean mIsSignedInDetected;

    private ActivityForResult mGoogleSignInActivityForResult;

    @Inject
    public SignInPresenter() {
        // See:
        //
        // http://stackoverflow.com/questions/37674823/firebase-android-onauthstatechanged-fire-twice-after-signinwithemailandpasswor
        //
        mAuthListener = firebaseAuth -> {
            SignInView view = mView;
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                if (!mIsSignedInDetected) {
                    LOG.d("signedIn: " + user.getUid());
                    view.showTangoPermissionFragment();
                    mIsSignedInDetected = true;
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
        mView = view;

        // Configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mResources.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(view.getActivity())
                .enableAutoManage(view.getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mGoogleSignInActivityForResult = new ActivityForResult
                .Builder(mView.getFragment(), Auth.GoogleSignInApi.getSignInIntent(googleApiClient))
                .setListener(this::onGoogleSignInActivityResult)
                .build();
    }

    public void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        mCompositeSubscription.clear();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LOG.e("onConnectionFailed: " + connectionResult);
        mView.showGoogleApiClientConnectionFailedSnackbar();
    }

    public void onSignIn() {
        mGoogleSignInActivityForResult.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGoogleSignInActivityForResult.onActivityResult(requestCode, resultCode, data);
    }

    private void onGoogleSignInActivityResult(Intent intent, boolean isCanceled) {
        SignInView view = mView;

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
