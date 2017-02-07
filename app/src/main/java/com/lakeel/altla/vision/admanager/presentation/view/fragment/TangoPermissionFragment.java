package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.tango.TangoIntents;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.TangoPermissionPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.TangoPermissionView;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class TangoPermissionFragment extends AbstractFragment<TangoPermissionView, TangoPermissionPresenter>
        implements TangoPermissionView {

    @Inject
    TangoPermissionPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    private InteractionListener interactionListener;

    public static TangoPermissionFragment newInstance() {
        return new TangoPermissionFragment();
    }

    @Override
    public TangoPermissionPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected TangoPermissionView getViewInterface() {
        return this;
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
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
        return inflater.inflate(R.layout.fragment_tango_permissions, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean isCanceled = (Activity.RESULT_CANCELED == resultCode);
        presenter.onTangoPermissionResult(isCanceled);
    }

    @Override
    public void onCloseTangoPermissionView() {
        interactionListener.onCloseTangoPermissionView();
    }

    @Override
    public void onShowAreaLearningPermissionRequiredSnackbar() {
        Snackbar.make(viewTop, R.string.snackbar_area_learning_permission_required, Snackbar.LENGTH_SHORT)
                .setAction(R.string.snackbar_action_request_permission, view -> presenter.onConfirmPermission())
                .show();
    }

    @Override
    public void onShowTangoPermissionActivity() {
        startActivityForResult(TangoIntents.createAdfLoadSaveRequestPermissionIntent(), 0);
    }

    public interface InteractionListener {

        void onCloseTangoPermissionView();
    }
}
