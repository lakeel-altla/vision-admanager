package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lakeel.altla.tango.TangoIntents;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserAreaDescriptionPresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.ImportStatus;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionView;
import com.lakeel.altla.vision.admanager.presentation.view.helper.DateFormatHelper;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

    @BindView(R.id.text_view_in_tango)
    TextView textViewInTango;

    @BindView(R.id.text_view_in_cloud)
    TextView textViewInCloud;

    @BindView(R.id.text_view_in_cache)
    TextView textViewInCache;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    @BindView(R.id.text_view_area_name)
    TextView textViewAreaName;

    @BindView(R.id.text_view_created_at)
    TextView textViewCreatedAt;

    @BindView(R.id.text_view_updated_at)
    TextView textViewUpdatedAt;

    private InteractionListener interactionListener;

    private boolean actionImportEnabled;

    private boolean actionUploadEnabled;

    private boolean actionDownloadEnabled;

    private boolean actionDeleteCacheEnabled;

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
        return inflater.inflate(R.layout.fragment_user_area_description, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_area_description, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_import).setVisible(actionImportEnabled);
        menu.findItem(R.id.action_upload).setVisible(actionUploadEnabled);
        menu.findItem(R.id.action_download).setVisible(actionDownloadEnabled);
        menu.findItem(R.id.action_delete_cache).setVisible(actionDeleteCacheEnabled);

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
    public void onUpdateTitle(@Nullable String name) {
        getActivity().setTitle(name);
    }

    @Override
    public void onUpdateAreaDescriptionId(@NonNull String areaDescriptionId) {
        textViewId.setText(areaDescriptionId);
    }

    @Override
    public void onUpdateImportStatus(@NonNull ImportStatus importStatus) {
        String text = null;
        switch (importStatus) {
            case IMPORTED:
                text = getString(R.string.field_imported);
                break;
            case NOT_IMPORTED:
                text = getString(R.string.field_not_imported);
                break;
            case UNKNOWN:
            default:
                break;
        }
        textViewInTango.setText(text);
    }

    @Override
    public void onUpdateFileUploaded(boolean fileUploaded) {
        String text = getString(fileUploaded ? R.string.field_uploaded : R.string.field_not_uploaded);
        textViewInCloud.setText(text);
    }

    @Override
    public void onUpdateFileCached(boolean fileCached) {
        String text = getString(fileCached ? R.string.field_cached : R.string.field_not_cached);
        textViewInCache.setText(text);
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
    public void onUpdateActionImport(boolean enabled) {
        actionImportEnabled = enabled;
        interactionListener.onInvalidateOptionsMenu();
    }

    @Override
    public void onUpdateActionUpload(boolean enabled) {
        actionUploadEnabled = enabled;
        interactionListener.onInvalidateOptionsMenu();
    }

    @Override
    public void onUpdateActionDownload(boolean enabled) {
        actionDownloadEnabled = enabled;
        interactionListener.onInvalidateOptionsMenu();
    }

    @Override
    public void onUpdateActionDeleteCache(boolean enabled) {
        actionDeleteCacheEnabled = enabled;
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
    public void onBackView() {
        interactionListener.onBackView();
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onShowUserAreaDescriptionEditView(@NonNull String areaDescriptionId);

        void onBackView();

        void onInvalidateOptionsMenu();
    }
}
