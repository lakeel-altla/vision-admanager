package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.SignInPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.SignInView;
import com.lakeel.altla.vision.admanager.presentation.view.activity.ActivityScopeContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class SignInFragment extends Fragment implements SignInView, GoogleApiClient.OnConnectionFailedListener {

    @Inject
    SignInPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    private InteractionListener interactionListener;

    private ProgressDialog progressDialog;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ActivityScopeContext.class.cast(getContext()).getUserComponent().inject(this);

        interactionListener = InteractionListener.class.cast(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean isCanceled = (Activity.RESULT_CANCELED == resultCode);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        presenter.onSignInResult(isCanceled, result);
    }

    @Override
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
        }

        progressDialog.setMessage(getString(R.string.progress_dialog_signin_in));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    public void showGoogleApiClientConnectionFailedSnackbar() {
        Snackbar.make(viewTop, R.string.snackbar_google_api_client_connection_failed, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showGoogleSignInFailedSnackbar() {
        Snackbar.make(viewTop, R.string.snackbar_google_sign_in_failed, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showGoogleSignInRequiredSnackbar() {
        Snackbar.make(viewTop, R.string.snackbar_google_sign_in_reqiured, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showSignedOutSnackbar() {
        Snackbar.make(viewTop, R.string.snackbar_signed_out, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTangoPermissionFragment() {
        interactionListener.onShowTangoPermissionFragment();
    }

    @Override
    public void startGoogleSignInActivity(GoogleSignInOptions options) {
        GoogleApiClient apiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(apiClient), 0);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        presenter.onGoogleApiClientConnectionFailed(connectionResult);
    }

    @OnClick(R.id.button_sign_in)
    void onClickButtonSignIn() {
        presenter.onSignIn();
    }

    public interface InteractionListener {

        void onShowTangoPermissionFragment();
    }
}
