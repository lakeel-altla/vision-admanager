package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lakeel.altla.tango.TangoIntents;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.TangoAreaDescriptionPresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoAreaDescriptionModel;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionView;

import android.app.Activity;
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

public final class TangoAreaDescriptionFragment
        extends AbstractFragment<TangoAreaDescriptionView, TangoAreaDescriptionPresenter>
        implements TangoAreaDescriptionView {

    @Inject
    TangoAreaDescriptionPresenter presenter;

    @BindView(R.id.view_top)
    View view;

    @BindView(R.id.text_view_id)
    TextView textViewId;

    @BindView(R.id.text_view_created_at)
    TextView textViewCreatedAt;

    @BindView(R.id.text_view_in_cloud)
    TextView textViewInCloud;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    private InteractionListener interactionListener;

    private MaterialDialog materialDialog;

    private boolean actionExportEnabled;

    @NonNull
    public static TangoAreaDescriptionFragment newInstance(@NonNull String areaDescriptionId) {
        TangoAreaDescriptionFragment fragment = new TangoAreaDescriptionFragment();
        Bundle bundle = TangoAreaDescriptionPresenter.createArguments(areaDescriptionId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected TangoAreaDescriptionPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected TangoAreaDescriptionView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_tango_area_description, container, false);
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
        inflater.inflate(R.menu.fragment_tango_area_description, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.action_export).setVisible(actionExportEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                presenter.onActionExport();
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
            presenter.onExported();
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onModelUpdated(@NonNull TangoAreaDescriptionModel model) {
        textViewId.setText(model.areaDescriptionId);

        String createdAtString = null;
        if (0 < model.createdAt) {
            createdAtString = DateFormat.getDateFormat(getContext()).format(model.createdAt) + " " +
                              DateFormat.getTimeFormat(getContext()).format(model.createdAt);
        }
        textViewCreatedAt.setText(createdAtString);

        String exportedString = getString(model.exported ? R.string.field_exported : R.string.field_not_exported);
        textViewInCloud.setText(exportedString);

        textViewName.setText(model.name);

        getActivity().setTitle(model.name);
    }

    @Override
    public void onUpdateActionExport(boolean enabled) {
        actionExportEnabled = enabled;
        interactionListener.onInvalidateOptionsMenu();
    }

    @Override
    public void onShowTangoAreaDescriptionExportActivity(@NonNull String uuid, @NonNull File destinationDirectory) {
        Intent intent = TangoIntents.createAdfExportIntent(uuid, destinationDirectory.getPath());
        startActivityForResult(intent, 0);
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
        interactionListener.onCloseTangoAreaDescriptionView();
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onCloseTangoAreaDescriptionView();

        void onInvalidateOptionsMenu();
    }
}
