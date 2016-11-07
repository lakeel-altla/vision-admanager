package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.TangoPermissionPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.TangoPermissionView;
import com.lakeel.altla.vision.admanager.presentation.view.activity.ActivityScopeContext;

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

public class TangoPermissionFragment extends Fragment implements TangoPermissionView {

    @Inject
    TangoPermissionPresenter mPresenter;

    @BindView(R.id.view_top)
    View mViewTop;

    private OnStartManagerActivityListener mListener;

    public static TangoPermissionFragment newInstance() {
        return new TangoPermissionFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = OnStartManagerActivityListener.class.cast(context);

        // Dagger
        ActivityScopeContext.class.cast(getContext()).getUserComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tango_permissions, container, false);
        ButterKnife.bind(this, view);

        mPresenter.onCreateView(this);
        mPresenter.onConfirmPermission();

        return view;
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
    public void startManagerActivity() {
        mListener.onStartManagerActivity();
    }

    @Override
    public void showAreaLearningPermissionRequiredSnackbar() {
        Snackbar.make(mViewTop, R.string.snackbar_area_learning_permission_required, Snackbar.LENGTH_SHORT)
                .setAction(R.string.snackbar_action_request_permission, view -> mPresenter.onConfirmPermission())
                .show();
    }

    public interface OnStartManagerActivityListener {

        void onStartManagerActivity();
    }
}
