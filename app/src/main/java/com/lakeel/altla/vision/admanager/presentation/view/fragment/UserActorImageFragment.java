package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserActorImagePresenter;
import com.lakeel.altla.vision.admanager.presentation.view.UserActorImageView;
import com.lakeel.altla.vision.admanager.presentation.view.helper.DateFormatHelper;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserActorImageFragment extends AbstractFragment<UserActorImageView, UserActorImagePresenter>
        implements UserActorImageView {

    @Inject
    UserActorImagePresenter presenter;

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

    @NonNull
    public static UserActorImageFragment newInstance(@NonNull String imageId) {
        UserActorImageFragment fragment = new UserActorImageFragment();
        Bundle bundle = UserActorImagePresenter.createArguments(imageId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected UserActorImagePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserActorImageView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_user_actor_image, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);
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
        Drawable placeholderDrawable = getContext().getResources().getDrawable(R.drawable.progress_animation);
        int placeholderTint = getContext().getResources().getColor(R.color.tint_progress);
        placeholderDrawable.setColorFilter(placeholderTint, PorterDuff.Mode.SRC_ATOP);

        Picasso picasso = Picasso.with(getContext());
        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);

        picasso.load(uri)
               .placeholder(placeholderDrawable)
               .error(R.drawable.ic_clear_black_24dp)
               .into(imageViewThumbnail);
    }

    @Override
    public void onUpdateName(@NonNull String name) {
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
        // TODO
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

    }
}
