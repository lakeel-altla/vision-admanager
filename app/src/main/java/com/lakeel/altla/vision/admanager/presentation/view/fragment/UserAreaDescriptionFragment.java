package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lakeel.altla.tango.TangoIntents;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserAreaDescriptionPresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserAreaDescriptionFragment
        extends AbstractFragment<UserAreaDescriptionView, UserAreaDescriptionPresenter>
        implements UserAreaDescriptionView {

    @Inject
    UserAreaDescriptionPresenter presenter;

    @BindView(R.id.view_top)
    View view;

    @BindView(R.id.text_view_id)
    TextView textViewId;

    @BindView(R.id.text_view_created_at)
    TextView textViewCreatedAt;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    @BindView(R.id.text_view_area_name)
    TextView textViewAreaName;

    private InteractionListener interactionListener;

    private boolean uploadMenuEnabled;

    private boolean downloadMenuEnabled;

    private boolean deleteCacheEnabled;

    private ProgressDialog progressDialog;

    private MaterialDialog materialDialog;

    @NonNull
    public static UserAreaDescriptionFragment newInstance(@NonNull String areaDescriptionId) {
        UserAreaDescriptionFragment fragment = new UserAreaDescriptionFragment();
        Bundle bundle = UserAreaDescriptionPresenter.createArguments(areaDescriptionId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected UserAreaDescriptionPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserAreaDescriptionView getViewInterface() {
        return this;
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

    @Override
    protected void onInject(@NonNull ActivityComponent component) {
        super.onInject(component);

        component.inject(this);
    }

    @Nullable
    @Override
    protected View onCreateViewCore(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_area_description, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        // Reset the title of the previous view.
        getActivity().setTitle(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_area_description, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_upload).setVisible(uploadMenuEnabled);
        menu.findItem(R.id.action_download).setVisible(downloadMenuEnabled);
        menu.findItem(R.id.action_delete_cache).setVisible(deleteCacheEnabled);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                presenter.onActionImport();
                return true;
            case R.id.action_upload:
                presenter.onActionUpload();
                return true;
            case R.id.action_download:
                presenter.onActionDownload();
                return true;
            case R.id.action_delete_cache:
                presenter.onActionDeleteCache();
                return true;
            case R.id.action_edit:
                presenter.onActionEdit();
                return true;
            case R.id.action_delete:
                presenter.onActionDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void onModelUpdated(@NonNull UserAreaDescriptionModel model) {
        textViewId.setText(model.areaDescriptionId);
        String createdAtString = null;
        if (0 < model.createdAt) {
            createdAtString = DateFormat.getDateFormat(getContext()).format(model.createdAt) + " " +
                              DateFormat.getTimeFormat(getContext()).format(model.createdAt);
        }
        textViewCreatedAt.setText(createdAtString);
        textViewName.setText(model.name);
        textViewAreaName.setText(model.areaName);

        getActivity().setTitle(model.name);
    }

    @Override
    public void onShowImportActivity(@NonNull File destinationFile) {
        Intent intent = TangoIntents.createAdfImportIntent(destinationFile.getPath());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onShowProgressDialog(@StringRes int messageResId) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(messageResId));
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(0);
        progressDialog.show();
    }

    @Override
    public void onProgressUpdated(long totalBytes, long increment) {
        if (progressDialog != null) {
            progressDialog.setMax((int) totalBytes);
            progressDialog.incrementProgressBy((int) increment);
        }
    }

    @Override
    public void onHideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    @Override
    public void onUpdateUploadMenu(boolean enabled) {
        uploadMenuEnabled = enabled;
        interactionListener.onInvalidateOptionsMenu();
    }

    @Override
    public void onUpdateDownloadMenu(boolean enabled) {
        downloadMenuEnabled = enabled;
        interactionListener.onInvalidateOptionsMenu();
    }

    @Override
    public void onUpdateDeleteCacheMenu(boolean enabled) {
        deleteCacheEnabled = enabled;
        interactionListener.onInvalidateOptionsMenu();
    }

    @Override
    public void onShowUserAreaDescriptionEditView(@NonNull String areaDescriptionId) {
        interactionListener.onShowUserAreaDescriptionEditView(areaDescriptionId);
    }

    @Override
    public void onShowDeleteConfirmationDialog() {
        if (materialDialog != null && materialDialog.isShowing()) {
            // Skip to protect against double taps.
            return;
        }

        materialDialog = new MaterialDialog.Builder(getContext())
                .content(R.string.dialog_content_confirm_delete)
                .positiveText(R.string.dialog_ok)
                .negativeText(R.string.dialog_cancel)
                .onPositive((dialog, which) -> presenter.onDelete())
                .build();

        materialDialog.show();
    }

    @Override
    public void onDeleted() {
        interactionListener.onCloseUserAreaDescriptionView();
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onShowUserAreaDescriptionEditView(@NonNull String areaDescriptionId);

        void onCloseUserAreaDescriptionView();

        void onInvalidateOptionsMenu();
    }
}
