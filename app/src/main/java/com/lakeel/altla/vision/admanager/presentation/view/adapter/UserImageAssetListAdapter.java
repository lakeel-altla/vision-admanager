package com.lakeel.altla.vision.admanager.presentation.view.adapter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserImageAssetListPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.UserImageAssetItemView;
import com.lakeel.altla.vision.admanager.presentation.view.helper.ThumbnailLoader;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserImageAssetListAdapter extends RecyclerView.Adapter<UserImageAssetListAdapter.ViewHolderAsset> {

    private final UserImageAssetListPresenter presenter;

    private final Context context;

    private final ThumbnailLoader thumbnailLoader;

    private RecyclerView recyclerView;

    private LayoutInflater inflater;

    public UserImageAssetListAdapter(@NonNull UserImageAssetListPresenter presenter, @NonNull Context context) {
        this.presenter = presenter;
        this.context = context;
        thumbnailLoader = new ThumbnailLoader(context);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public final ViewHolderAsset onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = inflater.inflate(R.layout.item_user_asset_image, parent, false);
        itemView.setOnClickListener(v -> {
            if (recyclerView != null) {
                int position = recyclerView.getChildAdapterPosition(v);
                presenter.onClickItem(position);
            }
        });
        return new ViewHolderAsset(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolderAsset holder, int position) {
        holder.itemPresenter.onBind(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    class ViewHolderAsset extends RecyclerView.ViewHolder implements UserImageAssetItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.image_view_thumbnail)
        ImageView imageViewThumbnail;

        private UserImageAssetListPresenter.ItemPresenter itemPresenter;

        ViewHolderAsset(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemPresenter = presenter.createItemPresenter();
            itemPresenter.onCreateItemView(this);
        }

        @Override
        public void onUpdateName(@Nullable String name) {
            textViewName.setText(name);
        }

        @Override
        public void onUpdateThumbnail(@Nullable Uri uri) {
            if (uri == null) {
                imageViewThumbnail.setImageBitmap(null);
            } else {
                thumbnailLoader.load(uri, imageViewThumbnail);
            }
        }
    }
}
