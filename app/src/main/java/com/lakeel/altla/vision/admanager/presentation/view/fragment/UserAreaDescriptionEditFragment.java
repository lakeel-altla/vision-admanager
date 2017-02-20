package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.google.android.gms.common.api.GoogleApiClient;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserAreaDescriptionEditPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionEditView;
import com.lakeel.altla.vision.domain.usecase.SaveUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public final class UserAreaDescriptionEditFragment
        extends AbstractFragment<UserAreaDescriptionEditView, UserAreaDescriptionEditPresenter>
        implements UserAreaDescriptionEditView {

    @Inject
    UserAreaDescriptionEditPresenter presenter;

    @Inject
    SaveUserAreaDescriptionUseCase saveUserAreaDescriptionUseCase;

    @Inject
    AppCompatActivity activity;

    @Inject
    GoogleApiClient googleApiClient;

    @BindView(R.id.view_top)
    View view;

    @BindView(R.id.text_input_layput_name)
    TextInputLayout textInputLayoutName;

    @BindView(R.id.text_input_edit_text_name)
    TextInputEditText textInputEditTextName;

    @BindView(R.id.image_button_select_area)
    ImageButton imageButtonSelectArea;

    @BindView(R.id.text_view_area_name)
    TextView textViewAreaName;

    private InteractionListener interactionListener;

    private boolean actionSaveEnabled;

    @NonNull
    public static UserAreaDescriptionEditFragment newInstance(String areaDescriptionId) {
        UserAreaDescriptionEditFragment fragment = new UserAreaDescriptionEditFragment();
        Bundle bundle = UserAreaDescriptionEditPresenter.createArguments(areaDescriptionId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public UserAreaDescriptionEditPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserAreaDescriptionEditView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_user_area_description_edit, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        // Close the soft keyboard when the user taps enter key.
        textInputEditTextName.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_area_description_edit, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_save).setVisible(actionSaveEnabled);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                presenter.onActionSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUpdateHomeAsUpIndicator(@DrawableRes int resId) {
        ActionBar actionBar = AppCompatActivity.class.cast(getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(resId);
        }
    }

    @Override
    public void onUpdateHomeAsUpIndicator(@Nullable Drawable drawable) {
        ActionBar actionBar = AppCompatActivity.class.cast(getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(null);
        }
    }

    @Override
    public void onUpdateViewsEnabled(boolean enabled) {
        textInputEditTextName.setEnabled(enabled);
        imageButtonSelectArea.setEnabled(enabled);

        int enabledTint = getResources().getColor(R.color.tint_image_button_enabled);
        int disabledTint = getResources().getColor(R.color.tint_image_button_disabled);
        int tint = enabled ? enabledTint : disabledTint;

        imageButtonSelectArea.setColorFilter(tint);
    }

    @Override
    public void onUpdateActionSave(boolean enabled) {
        actionSaveEnabled = enabled;
        interactionListener.onInvalidateOptionsMenu();
    }

    @Override
    public void onUpdateTitle(@Nullable String title) {
        getActivity().setTitle(title);
    }

    @Override
    public void onUpdateName(String name) {
        textInputEditTextName.setText(name);
    }

    @Override
    public void onUpdateAreaName(String areaName) {
        textViewAreaName.setText(areaName);
    }

    @Override
    public void onShowNameError(@StringRes int resId) {
        textInputLayoutName.setError(getString(resId));
    }

    @Override
    public void onHideNameError() {
        textInputLayoutName.setError(null);
    }

    @Override
    public void onShowUserAreaSelectView() {
        interactionListener.onShowUserAreaSelectView();
    }

    @Override
    public void onBackView() {
        interactionListener.onBackView();
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
    }

    public void onUserAreaSelected(@NonNull String areaId) {
        presenter.onUserAreaSelected(areaId);
    }

    @OnTextChanged(value = R.id.text_input_edit_text_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onEditTextNameAfterTextChanged(Editable editable) {
        presenter.onEditTextNameAfterTextChanged(editable.toString());
    }

    @OnClick(R.id.image_button_select_area)
    void onClickImageButtonSelectArea() {
        presenter.onClickImageButtonSelectArea();
    }

    public interface InteractionListener {

        void onInvalidateOptionsMenu();

        void onShowUserAreaSelectView();

        void onBackView();
    }
}
