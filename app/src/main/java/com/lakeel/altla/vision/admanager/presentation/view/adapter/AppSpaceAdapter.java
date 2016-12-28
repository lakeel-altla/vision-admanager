package com.lakeel.altla.vision.admanager.presentation.view.adapter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.AppSpacePresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceItemView;

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

public final class AppSpaceAdapter extends RecyclerView.Adapter<AppSpaceAdapter.ViewHolder> {

    private final AppSpacePresenter presenter;

    private LayoutInflater inflater;

    public AppSpaceAdapter(@NonNull AppSpacePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = inflater.inflate(R.layout.item_app_space_model, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        presenter.onCreateItemView(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AppSpaceItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewUuid;

        @BindView(R.id.image_button_upload)
        ImageButton imageButtonUpload;

        @BindView(R.id.image_button_download)
        ImageButton imageButtonDownload;

        @BindView(R.id.image_button_synced)
        ImageButton imageButtonSynced;

        private AppSpacePresenter.AppSpaceItemPresenter itemPresenter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setItemPresenter(@NonNull AppSpacePresenter.AppSpaceItemPresenter itemPresenter) {
            this.itemPresenter = itemPresenter;
        }

        @Override
        public void showModel(@NonNull AppSpaceItemModel model) {
            textViewName.setText(model.name);
            textViewUuid.setText(model.areaDescriptionId);

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

        public void onBind(int position) {
            itemPresenter.onBind(position);
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
