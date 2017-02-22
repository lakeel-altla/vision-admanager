package com.lakeel.altla.vision.admanager.presentation.view.adapter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserActorImageListPresenter;
import com.lakeel.altla.vision.admanager.presentation.view.UserActorImageItemView;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserActorImageListAdapter extends RecyclerView.Adapter<UserActorImageListAdapter.ViewHolder> {

    private final UserActorImageListPresenter presenter;

    private RecyclerView recyclerView;

    private LayoutInflater inflater;

    public UserActorImageListAdapter(@NonNull UserActorImageListPresenter presenter) {
        this.presenter = presenter;
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
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = inflater.inflate(R.layout.item_user_actor_image, parent, false);
        itemView.setOnClickListener(v -> {
            if (recyclerView != null) {
                int position = recyclerView.getChildAdapterPosition(v);
                presenter.onClickItem(position);
            }
        });
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemPresenter.onBind(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements UserActorImageItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.image_view_thumbnail)
        ImageView imageViewThumbnail;

        @BindView(R.id.progress_bar_thumbnail)
        ProgressBar progressBarThumbnail;

        private UserActorImageListPresenter.ItemPresenter itemPresenter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemPresenter = presenter.createItemPresenter();
            itemPresenter.onCreateItemView(this);
        }

        @Override
        public void onUpdateName(@NonNull String name) {
            textViewName.setText(name);
        }

        @Override
        public void onUpdateThumbnail(@NonNull Bitmap bitmap) {
            imageViewThumbnail.setImageBitmap(bitmap);
        }

        @Override
        public void onUpdateProgressRingThumbnailVisible(boolean visible) {
            progressBarThumbnail.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
