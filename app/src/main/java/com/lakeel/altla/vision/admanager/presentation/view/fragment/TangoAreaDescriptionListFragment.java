package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoIntents;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.TangoAreaDescriptionListPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionListView;
import com.lakeel.altla.vision.admanager.presentation.view.adapter.TangoAreaDescriptionListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class TangoAreaDescriptionListFragment extends Fragment implements TangoAreaDescriptionListView {

    private static final Log LOG = LogFactory.getLog(TangoAreaDescriptionListFragment.class);

    @Inject
    TangoAreaDescriptionListPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private MaterialDialog materialDialog;

    public static TangoAreaDescriptionListFragment newInstance() {
        return new TangoAreaDescriptionListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tango_area_description_list, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        recyclerView.setAdapter(new TangoAreaDescriptionListAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().setTitle(R.string.title_tango_space);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        LOG.d("onActivityResult: requestCode = %d, resultCode = %d, intent = %s", requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            presenter.onExported();
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void updateItems() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateItemRemoved(int position) {
        recyclerView.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showExportActivity(@NonNull String uuid, @NonNull File destinationDirectory) {
        Intent intent = TangoIntents.createAdfExportIntent(uuid, destinationDirectory.getPath());
        startActivityForResult(intent, 0);
    }

    @Override
    public void showDeleteConfirmationDialog(int position) {
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
