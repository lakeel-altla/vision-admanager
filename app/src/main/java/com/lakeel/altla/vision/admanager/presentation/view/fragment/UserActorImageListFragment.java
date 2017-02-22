package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserActorImageListPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.UserActorImageListView;
import com.lakeel.altla.vision.admanager.presentation.view.adapter.UserActorImageListAdapter;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserActorImageListFragment
        extends AbstractFragment<UserActorImageListView, UserActorImageListPresenter>
        implements UserActorImageListView {

    @Inject
    UserActorImageListPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private InteractionListener interactionListener;

    @Inject
    public UserActorImageListFragment() {
    }

    public static UserActorImageListFragment newInstance() {
        return new UserActorImageListFragment();
    }

    @Override
    protected UserActorImageListPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserActorImageListView getViewInterface() {
        return this;
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        interactionListener = InteractionListener.class.cast(context);
        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
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
        return inflater.inflate(R.layout.fragment_user_actor_image_list, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new UserActorImageListAdapter(presenter, getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_actor_image_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                interactionListener.onShowUserActorImageCreateView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUpdateTitle(@StringRes int resId) {
        getActivity().setTitle(resId);
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
    public void onItemSelected(String imageId) {
        interactionListener.onShowUserActorImageView(imageId);
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onShowUserActorImageView(@NonNull String imageId);

        void onShowUserActorImageCreateView();
    }
}
