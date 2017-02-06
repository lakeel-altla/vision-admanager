package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lakeel.altla.tango.TangoIntents;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserAreaDescriptionListPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionListView;
import com.lakeel.altla.vision.admanager.presentation.view.adapter.UserAreaDescriptionListAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

public final class UserAreaDescriptionListFragment
        extends AbstractFragment<UserAreaDescriptionListView, UserAreaDescriptionListPresenter>
        implements UserAreaDescriptionListView {

    @Inject
    UserAreaDescriptionListPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private InteractionListener interactionListener;

    private ProgressDialog progressDialog;

    private MaterialDialog materialDialog;

    public static UserAreaDescriptionListFragment newInstance() {
        return new UserAreaDescriptionListFragment();
    }

    @Override
    public UserAreaDescriptionListPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserAreaDescriptionListView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_user_area_description_list, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new UserAreaDescriptionListAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().setTitle(R.string.title_user_area_description_list);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            presenter.onImported();
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
    public void onShowImportActivity(@NonNull File destinationFile) {
        Intent intent = TangoIntents.createAdfImportIntent(destinationFile.getPath());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onShowUploadProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.progress_dialog_upload));
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(0);
        progressDialog.show();
    }

    @Override
    public void onUploadProgressUpdated(long max, long diff) {
        if (progressDialog != null) {
            progressDialog.setMax((int) max);
            progressDialog.incrementProgressBy((int) diff);
        }
    }

    @Override
    public void onHideUploadProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    @Override
    public void onShowDeleteProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.progress_dialog_delete));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onHideDeleteProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog = null;
        }
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

    @Override
    public void onShowUserAreaDescriptionEditView(String areaDescriptionId) {
        interactionListener.onShowUserAreaDescriptionEditView(areaDescriptionId);
    }

    public interface InteractionListener {

        void onShowUserAreaDescriptionEditView(String areaDescriptionId);
    }
}
