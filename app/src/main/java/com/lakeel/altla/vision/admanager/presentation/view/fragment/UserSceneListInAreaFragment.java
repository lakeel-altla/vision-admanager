package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserSceneListInAreaPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneListInAreaView;
import com.lakeel.altla.vision.admanager.presentation.view.adapter.UserSceneListInAreaAdapter;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserSceneListInAreaFragment
        extends AbstractFragment<UserSceneListInAreaView, UserSceneListInAreaPresenter>
        implements UserSceneListInAreaView {

    @Inject
    UserSceneListInAreaPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private InteractionListener interactionListener;

    public static UserSceneListInAreaFragment newInstance(@NonNull String areaId) {
        UserSceneListInAreaFragment fragment = new UserSceneListInAreaFragment();
        Bundle bundle = UserSceneListInAreaPresenter.createArguments(areaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected UserSceneListInAreaPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserSceneListInAreaView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_user_scene_list_in_area, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new UserSceneListInAreaAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onUpdateTitle(@Nullable String title) {
        getActivity().setTitle(title);
    }

    @Override
    public void onItemInserted(int position) {
        recyclerView.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void onItemChanged(int position) {
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void onItemRemoved(int position) {
        recyclerView.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onDataSetChanged() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(String sceneId) {
        interactionListener.onShowUserSceneView(sceneId);
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onShowUserSceneView(@NonNull String sceneId);
    }
}
