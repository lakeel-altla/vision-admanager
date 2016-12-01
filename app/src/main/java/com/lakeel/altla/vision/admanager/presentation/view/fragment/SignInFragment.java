package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.SignInPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.SignInView;
import com.lakeel.altla.vision.admanager.presentation.view.activity.ActivityScopeContext;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInFragment extends Fragment implements SignInView {

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

        interactionListener = InteractionListener.class.cast(context);

        // Dagger
        ActivityScopeContext.class.cast(getContext()).getUserComponent().inject(this);
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
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Fragment getFragment() {
        return this;
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
    public void showFirebaseSignInFailedSnackbar() {
        Snackbar.make(viewTop, R.string.snackbar_firebase_sign_in_failed, Snackbar.LENGTH_SHORT).show();
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

    @OnClick(R.id.button_sign_in)
    void onClickButtonSignIn() {
        presenter.onSignIn();
    }

    public interface InteractionListener {

        void onShowTangoPermissionFragment();
    }
}
