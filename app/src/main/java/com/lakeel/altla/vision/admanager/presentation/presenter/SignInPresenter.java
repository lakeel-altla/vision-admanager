package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.SignInView;
import com.lakeel.altla.vision.domain.usecase.SignInWithGoogleUseCase;

import android.app.Activity;
import android.content.Intent;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class SignInPresenter extends BasePresenter<SignInView> {

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 0;

    @Inject
    GoogleApiClient googleApiClient;

    @Inject
    SignInWithGoogleUseCase signInWithGoogleUseCase;

    private final FirebaseAuth.AuthStateListener authStateListener;

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
                    getLog().i("Signed in to firebase: %s", user.getUid());
                    if (!signInButtonClicked) {
                        // Auto sign in.
                        getView().onCloseSignInView();
                    }
                    signedInDetected = true;
                } else {
                    getLog().d("onAuthStateChanged() is fired twice.");
                }
            } else {
                getLog().d("Signed out.");
            }
        };
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    public void onClickButtonSignIn() {
        signInButtonClicked = true;

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);

        getView().onStartActivityForResult(intent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE_GOOGLE_SIGN_IN) {
            // Ignore.
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            getLog().d("Canceled to sign in to Google.");
            getView().onSnackbar(R.string.snackbar_google_sign_in_reqiured);
            return;
        }

        GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (!googleSignInResult.isSuccess()) {
            getLog().e("Failed to sign in to Google.");
            getView().onSnackbar(R.string.snackbar_google_sign_in_failed);
            return;
        }

        GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
        if (googleSignInAccount == null) {
            getLog().e("GoogleSignInAccount is null");
            getView().onSnackbar(R.string.snackbar_google_sign_in_failed);
            return;
        }

        Disposable disposable = signInWithGoogleUseCase
                .execute(googleSignInAccount)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_subscription -> getView().onShowProgressDialog())
                .doOnTerminate(() -> getView().onHideProgressDialog())
                .subscribe(() -> getView().onCloseSignInView(),
                           e -> getLog().e("Failed to sign in to Firebase.", e));
        manageDisposable(disposable);
    }
}
