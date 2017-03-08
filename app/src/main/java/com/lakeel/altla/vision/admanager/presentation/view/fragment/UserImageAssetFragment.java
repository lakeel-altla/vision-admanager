package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserImageAssetPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.UserImageAssetView;
import com.lakeel.altla.vision.admanager.presentation.view.helper.DateFormatHelper;
import com.lakeel.altla.vision.admanager.presentation.view.helper.ThumbnailLoader;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserImageAssetFragment extends AbstractFragment<UserImageAssetView, UserImageAssetPresenter>
        implements UserImageAssetView {

    @Inject
    UserImageAssetPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.text_view_id)
    TextView textViewId;

    @BindView(R.id.image_view_thumbnail)
    ImageView imageViewThumbnail;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    @BindView(R.id.text_view_created_at)
    TextView textViewCreatedAt;

    @BindView(R.id.text_view_updated_at)
    TextView textViewUpdatedAt;

    private InteractionListener interactionListener;

    private ThumbnailLoader thumbnailLoader;

    @NonNull
    public static UserImageAssetFragment newInstance(@NonNull String imageId) {
        UserImageAssetFragment fragment = new UserImageAssetFragment();
        Bundle bundle = UserImageAssetPresenter.createArguments(imageId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected UserImageAssetPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserImageAssetView getViewInterface() {
        return this;
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        interactionListener = InteractionListener.class.cast(context);
        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
        thumbnailLoader = new ThumbnailLoader(context);
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
        return inflater.inflate(R.layout.fragment_user_image_asset, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_image_asset, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                presenter.onEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUpdateTitle(@Nullable String name) {
        getActivity().setTitle(name);
    }

    @Override
    public void onUpdateImageId(@NonNull String imageId) {
        textViewId.setText(imageId);
    }

    @Override
    public void onUpdateThumbnail(@NonNull Uri uri) {
        thumbnailLoader.load(uri, imageViewThumbnail);
    }

    @Override
    public void onUpdateName(@Nullable String name) {
        textViewName.setText(name);
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
    public void onShowUserActorImageEditView(String imageId) {
        interactionListener.onShowUserActorImageEditView(imageId);
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onShowUserActorImageEditView(@NonNull String imageId);
    }
}
