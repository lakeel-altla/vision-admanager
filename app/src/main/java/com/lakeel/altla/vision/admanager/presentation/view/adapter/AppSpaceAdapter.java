package com.lakeel.altla.vision.admanager.presentation.view.adapter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.AppSpacePresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceItemView;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            textViewUuid.setText(model.id);
        }

        public void onBind(@IntRange(from = 0) int position) {
            itemPresenter.onBind(position);
        }

        @OnClick(R.id.button_import)
        void onClickImport() {
            itemPresenter.onImport(getAdapterPosition());
        }
    }
}
