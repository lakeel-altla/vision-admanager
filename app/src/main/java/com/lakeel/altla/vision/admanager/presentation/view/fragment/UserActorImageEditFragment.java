package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserActorImageEditPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.UserActorImageEditView;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public final class UserActorImageEditFragment
        extends AbstractFragment<UserActorImageEditView, UserActorImageEditPresenter>
        implements UserActorImageEditView {

    private static final int REQUEST_CODE_ACTION_OPEN_DOCUMENT = 0;

    @Inject
    UserActorImageEditPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.button_select_image)
    Button buttonSelectImage;

    @BindView(R.id.image_view_thumbnail)
    ImageView imageViewThumbnail;

    @BindView(R.id.progress_bar_thumbnail)
    ProgressBar progressBarThumbnail;

    @BindView(R.id.text_input_layout_name)
    TextInputLayout textInputLayoutName;

    @BindView(R.id.text_input_edit_text_name)
    TextInputEditText textInputEditTextName;

    private InteractionListener interactionListener;

    private boolean actionSaveEnabled;

    @NonNull
    public static UserActorImageEditFragment newInstance(@Nullable String imageId) {
        UserActorImageEditFragment fragment = new UserActorImageEditFragment();
        Bundle bundle = UserActorImageEditPresenter.createArguments(imageId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected UserActorImageEditPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserActorImageEditView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_user_actor_image_edit, container, false);
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
        inflater.inflate(R.menu.fragment_user_actor_image_edit, menu);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_ACTION_OPEN_DOCUMENT == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                Uri uri = (data != null) ? data.getData() : null;
                if (uri != null) {
                    presenter.onImageSelected(uri);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onUpdateViewsEnabled(boolean enabled) {
        buttonSelectImage.setEnabled(enabled);
        textInputEditTextName.setEnabled(enabled);
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
    public void onUpdateActionSave(boolean enabled) {
        actionSaveEnabled = enabled;
        interactionListener.onInvalidateOptionsMenu();
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
    public void onUpdateProgressBarThumbnailVisible(boolean visible) {
        progressBarThumbnail.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onShowImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*");

        startActivityForResult(intent, REQUEST_CODE_ACTION_OPEN_DOCUMENT);
    }

    @Override
    public void onUpdateTitle(@Nullable String title) {
        getActivity().setTitle(title);
    }

    @Override
    public void onUpdateThumbnail(@Nullable Bitmap bitmap) {
        imageViewThumbnail.setImageBitmap(bitmap);
    }

    @Override
    public void onUpdateName(@Nullable String name) {
        textInputEditTextName.setText(name);
    }

    @Override
    public void onBackView() {
        interactionListener.onBackView();
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_select_image)
    void onClickButtonSelectImage() {
        presenter.onClickButtonSelectImage();
    }

    @OnTextChanged(value = R.id.text_input_edit_text_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onEditTextNameAfterTextChanged(Editable editable) {
        presenter.onEditTextNameAfterTextChanged(editable.toString());
    }

    public interface InteractionListener {

        void onInvalidateOptionsMenu();

        void onBackView();
    }
}
