package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.presenter.TangoAreaDescriptionPresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoAreaDescriptionModel;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionView;

import android.content.Context;
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

    @BindView(R.id.text_view_name)
    TextView textViewName;

    private InteractionListener interactionListener;

    private MaterialDialog materialDialog;

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
        inflater.inflate(R.menu.fragment_user_area_description, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                presenter.onActionDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        textViewName.setText(model.name);

        getActivity().setTitle(model.name);
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
    }
}
