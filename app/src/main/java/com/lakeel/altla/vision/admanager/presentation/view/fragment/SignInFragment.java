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
    SignInPresenter mPresenter;

    @BindView(R.id.view_top)
    View mViewTop;

    private OnShowTangoPermissionFragmentListener mListener;

    private ProgressDialog mProgressDialog;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = OnShowTangoPermissionFragmentListener.class.cast(context);

        // Dagger
        ActivityScopeContext.class.cast(getContext()).getUserComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);

        mPresenter.onCreateView(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_signin_in));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void showGoogleApiClientConnectionFailedSnackbar() {
        Snackbar.make(mViewTop, R.string.snackbar_google_api_client_connection_failed, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showFirebaseSignInFailedSnackbar() {
        Snackbar.make(mViewTop, R.string.snackbar_firebase_sign_in_failed, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showGoogleSignInFailedSnackbar() {
        Snackbar.make(mViewTop, R.string.snackbar_google_sign_in_failed, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showGoogleSignInRequiredSnackbar() {
        Snackbar.make(mViewTop, R.string.snackbar_google_sign_in_reqiured, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showSignedOutSnackbar() {
        Snackbar.make(mViewTop, R.string.snackbar_signed_out, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTangoPermissionFragment() {
        mListener.onShowTangoPermissionFragment();
    }

    @OnClick(R.id.button_sign_in)
    void onClickButtonSignIn() {
        mPresenter.onSignIn();
    }

    public interface OnShowTangoPermissionFragmentListener {

        void onShowTangoPermissionFragment();
    }
}
