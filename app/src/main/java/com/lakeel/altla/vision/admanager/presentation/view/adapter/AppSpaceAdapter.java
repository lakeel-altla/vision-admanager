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

    private final AppSpacePresenter mPresenter;

    private LayoutInflater mInflater;

    public AppSpaceAdapter(@NonNull AppSpacePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = mInflater.inflate(R.layout.list_item_app_space_data, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        mPresenter.onCreateItemView(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mPresenter.getItemCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AppSpaceItemView {

        @BindView(R.id.text_view_name)
        TextView mTextViewName;

        @BindView(R.id.text_view_uuid)
        TextView mTextViewUuid;

        private AppSpacePresenter.AppSpaceItemPresenter mItemPresenter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setItemPresenter(@NonNull AppSpacePresenter.AppSpaceItemPresenter itemPresenter) {
            mItemPresenter = itemPresenter;
        }

        @Override
        public void showModel(@NonNull AppSpaceItemModel model) {
            mTextViewName.setText(model.name);
            mTextViewUuid.setText(model.uuid);
        }

        public void onBind(@IntRange(from = 0) int position) {
            mItemPresenter.onBind(position);
        }

        @OnClick(R.id.button_import)
        void onClickImport() {
            mItemPresenter.onImport(getAdapterPosition());
        }

        @OnClick(R.id.button_upload)
        void onClickUpload() {
            mItemPresenter.onUpload(getAdapterPosition());
        }
    }
}
