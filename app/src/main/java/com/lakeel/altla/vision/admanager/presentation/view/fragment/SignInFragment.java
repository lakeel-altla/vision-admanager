package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.SignInPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.SignInView;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class SignInFragment extends Fragment implements SignInView {

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

        ActivityScopeContext.class.cast(getContext()).getActivityComponent().inject(this);

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
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.progress_dialog_signin_in));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void closeSignInFragment() {
        interactionListener.onCloseSignInFragment();
    }

    @OnClick(R.id.button_sign_in)
    void onClickButtonSignIn() {
        presenter.onClickButtonSignIn();
    }

    public interface InteractionListener {

        void onCloseSignInFragment();
    }
}
