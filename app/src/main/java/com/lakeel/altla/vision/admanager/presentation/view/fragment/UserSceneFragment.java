package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserScenePresenter;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneView;
import com.lakeel.altla.vision.admanager.presentation.view.helper.DateFormatHelper;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserSceneFragment extends AbstractFragment<UserSceneView, UserScenePresenter>
        implements UserSceneView {

    @Inject
    UserScenePresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.text_view_id)
    TextView textViewId;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    @BindView(R.id.text_view_area_name)
    TextView textViewAreaName;

    @BindView(R.id.text_view_created_at)
    TextView textViewCreatedAt;

    @BindView(R.id.text_view_updated_at)
    TextView textViewUpdatedAt;

    private InteractionListener interactionListener;

    @NonNull
    public static UserSceneFragment newInstance(@NonNull String sceneId) {
        UserSceneFragment fragment = new UserSceneFragment();
        Bundle bundle = UserScenePresenter.createArguments(sceneId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public UserScenePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserSceneView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_user_scene, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_scene, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                presenter.onEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUpdateTitle(@Nullable String name) {
        getActivity().setTitle(name);
    }

    @Override
    public void onUpdateSceneId(@NonNull String sceneId) {
        textViewId.setText(sceneId);
    }

    @Override
    public void onUpdateName(@Nullable String name) {
        textViewName.setText(name);
    }

    @Override
    public void onUpdateAreaName(@Nullable String areaName) {
        textViewAreaName.setText(areaName);
    }

    @Override
    public void onUpdateCreatedAt(long createdAt) {
        textViewCreatedAt.setText(DateFormatHelper.format(getContext(), createdAt));
    }

    @Override
    public void onUpdateUpdatedAt(long updatedAt) {
        textViewUpdatedAt.setText(DateFormatHelper.format(getContext(), updatedAt));
    }

    @Override
    public void onShowUserSceneEditView(@NonNull String sceneId) {
        interactionListener.onShowUserSceneEditView(sceneId);
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onShowUserSceneEditView(@NonNull String sceneId);
    }
}

