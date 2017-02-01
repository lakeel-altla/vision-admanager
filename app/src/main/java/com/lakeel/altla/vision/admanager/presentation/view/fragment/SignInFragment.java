package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.presenter.SignInPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.SignInView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class SignInFragment extends AbstractFragment<SignInView, SignInPresenter> implements SignInView {

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
    public SignInPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected SignInView getViewInterface() {
        return this;
    }

    @Override
    protected void onInject(@NonNull ActivityComponent component) {
        super.onInject(component);

        component.inject(this);
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        interactionListener = InteractionListener.class.cast(context);
    }

    @Override
    protected void onDetachOverride() {
        super.onDetachOverride();

        interactionListener = null;
    }

    @Nullable
    @Override
    protected View onCreateViewCore(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);
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
