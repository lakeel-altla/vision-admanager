package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.SignInView;
import com.lakeel.altla.vision.domain.usecase.SignInWithGoogleUseCase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class SignInPresenter {

    private static final Log LOG = LogFactory.getLog(SignInPresenter.class);

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 0;

    @Inject
    GoogleApiClient googleApiClient;

    @Inject
    SignInWithGoogleUseCase signInWithGoogleUseCase;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private final FirebaseAuth.AuthStateListener authStateListener;

    private SignInView view;

    private boolean signedInDetected;

    private boolean signInButtonClicked;

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
                    LOG.i("Signed in to firebase: %s", user.getUid());
                    if (!signInButtonClicked) {
                        // Auto sign in.
                        view.closeSignInFragment();
                    }
                    signedInDetected = true;
                } else {
                    LOG.d("onAuthStateChanged() is fired twice.");
                }
            } else {
                LOG.d("Signed out.");
            }
        };
    }

    public void onCreateView(@NonNull SignInView view) {
        this.view = view;
    }

    public void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    public void onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        compositeSubscription.clear();
    }

    public void onClickButtonSignIn() {
        signInButtonClicked = true;

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);

        view.startActivityForResult(intent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE_GOOGLE_SIGN_IN) {
            // Ignore.
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            LOG.d("Canceled to sign in to Google.");
            view.showSnackbar(R.string.snackbar_google_sign_in_reqiured);
            return;
        }

        GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (!googleSignInResult.isSuccess()) {
            LOG.e("Failed to sign in to Google.");
            view.showSnackbar(R.string.snackbar_google_sign_in_failed);
            return;
        }

        GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
        if (googleSignInAccount == null) {
            LOG.e("GoogleSignInAccount is null");
            view.showSnackbar(R.string.snackbar_google_sign_in_failed);
            return;
        }

        Subscription subscription = signInWithGoogleUseCase
                .execute(googleSignInAccount)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_subscription -> view.showProgressDialog())
                .doOnUnsubscribe(() -> view.hideProgressDialog())
                .subscribe(() -> {
                    view.closeSignInFragment();
                }, e -> LOG.e("Failed to sign in to Firebase.", e));
        compositeSubscription.add(subscription);
    }
}
