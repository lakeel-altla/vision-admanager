package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.presenter.SelectUserAreaPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.SelectUserAreaView;
import com.lakeel.altla.vision.admanager.presentation.view.adapter.SelectUserAreaAdapter;

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

public final class SelectUserAreaFragment extends AbstractFragment<SelectUserAreaView, SelectUserAreaPresenter>
        implements SelectUserAreaView {

    @Inject
    SelectUserAreaPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private InteractionListener interactionListener;

    @NonNull
    public static SelectUserAreaFragment newInstance() {
        return new SelectUserAreaFragment();
    }

    @Override
    public SelectUserAreaPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected SelectUserAreaView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_select_user_area, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new SelectUserAreaAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().setTitle(R.string.title_select_user_area);
    }

    @Override
    public void updateItem(int position) {
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void updateItems() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(String areaId) {
        interactionListener.onUserAreaSelected(areaId);
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onUserAreaSelected(String areaId);
    }
}
