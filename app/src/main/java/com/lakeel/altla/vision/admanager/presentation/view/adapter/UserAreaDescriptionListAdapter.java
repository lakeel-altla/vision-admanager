package com.lakeel.altla.vision.admanager.presentation.view.adapter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserAreaDescriptionListPresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionItemView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class UserAreaDescriptionListAdapter
        extends RecyclerView.Adapter<UserAreaDescriptionListAdapter.ViewHolder> {

    private final UserAreaDescriptionListPresenter presenter;

    private RecyclerView recyclerView;

    private LayoutInflater inflater;

    public UserAreaDescriptionListAdapter(@NonNull UserAreaDescriptionListPresenter presenter) {
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
        View itemView = inflater.inflate(R.layout.item_user_area_description, parent, false);
        itemView.setOnClickListener(v -> {
            if (recyclerView != null) {
                int position = recyclerView.getChildAdapterPosition(itemView);
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

    class ViewHolder extends RecyclerView.ViewHolder implements UserAreaDescriptionItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewId;

        @BindView(R.id.image_button_upload)
        ImageButton imageButtonUpload;

        @BindView(R.id.image_button_download)
        ImageButton imageButtonDownload;

        @BindView(R.id.image_button_synced)
        ImageButton imageButtonSynced;

        private UserAreaDescriptionListPresenter.ItemPresenter itemPresenter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemPresenter = presenter.createItemPresenter();
            itemPresenter.onCreateItemView(this);
        }

        @Override
        public void onModelUpdated(@NonNull UserAreaDescriptionItemModel model) {
            textViewName.setText(model.name);
            textViewId.setText(model.areaDescriptionId);

            imageButtonUpload.setVisibility(View.GONE);
            imageButtonDownload.setVisibility(View.GONE);
            imageButtonSynced.setVisibility(View.GONE);

            if (model.fileUploaded && model.fileCached) {
                imageButtonSynced.setVisibility(View.VISIBLE);
            } else if (model.fileUploaded) {
                imageButtonDownload.setVisibility(View.VISIBLE);
            } else if (model.fileCached) {
                imageButtonUpload.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.image_button_import)
        void onClickImageButtonImport() {
            itemPresenter.onClickImageButtonImport(getAdapterPosition());
        }

        @OnClick(R.id.image_button_upload)
        void onClickImageButtonUpload() {
            itemPresenter.onClickImageButtonUpload(getAdapterPosition());
        }

        @OnClick(R.id.image_button_download)
        void onClickImageButtonDownload() {
            itemPresenter.onClickImageButtonDownload(getAdapterPosition());
        }

        @OnClick(R.id.image_button_synced)
        void onClickImageButtonSynced() {
            itemPresenter.onClickImageButtonSynced(getAdapterPosition());
        }

        @OnClick(R.id.image_button_edit)
        void onClickImageButtonEdit() {
            itemPresenter.onClickImageButtonEdit(getAdapterPosition());
        }

        @OnClick(R.id.image_button_delete)
        void onClickImageButtonDelete() {
            itemPresenter.onClickImageButtonDelete(getAdapterPosition());
        }
    }
}
