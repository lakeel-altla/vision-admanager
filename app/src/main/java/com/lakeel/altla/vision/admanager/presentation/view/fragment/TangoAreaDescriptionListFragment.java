package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lakeel.altla.tango.TangoIntents;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.presenter.TangoAreaDescriptionListPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionListView;
import com.lakeel.altla.vision.admanager.presentation.view.adapter.TangoAreaDescriptionListAdapter;

import android.app.Activity;
import android.content.Intent;
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

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class TangoAreaDescriptionListFragment
        extends AbstractFragment<TangoAreaDescriptionListView, TangoAreaDescriptionListPresenter>
        implements TangoAreaDescriptionListView {

    @Inject
    TangoAreaDescriptionListPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private MaterialDialog materialDialog;

    public static TangoAreaDescriptionListFragment newInstance() {
        return new TangoAreaDescriptionListFragment();
    }

    @Override
    public TangoAreaDescriptionListPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected TangoAreaDescriptionListView getViewInterface() {
        return this;
    }

    @Override
    protected void onInject(@NonNull ActivityComponent component) {
        super.onInject(component);

        component.inject(this);
    }

    @Nullable
    @Override
    protected View onCreateViewCore(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tango_area_description_list, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new TangoAreaDescriptionListAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().setTitle(R.string.title_tango_area_description_list);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            presenter.onExported();
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onItemsUpdated() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onItemInserted(int position) {
        recyclerView.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void onItemRemoved(int position) {
        recyclerView.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onExportActivity(@NonNull String uuid, @NonNull File destinationDirectory) {
        Intent intent = TangoIntents.createAdfExportIntent(uuid, destinationDirectory.getPath());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onShowDeleteConfirmationDialog(int position) {
        if (materialDialog != null && materialDialog.isShowing()) {
            // Skip to protect against double taps.
            return;
        }

        materialDialog = new MaterialDialog.Builder(getContext())
                .content(R.string.dialog_content_confirm_delete)
                .positiveText(R.string.dialog_ok)
                .negativeText(R.string.dialog_cancel)
                .onPositive((dialog, which) -> presenter.onDelete(position))
                .build();

        materialDialog.show();
    }
}
