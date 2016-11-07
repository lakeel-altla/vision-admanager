package com.lakeel.altla.vision.admanager.presentation.view.adapter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.TangoSpacePresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceItemView;

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

public final class TangoSpaceAdapter extends RecyclerView.Adapter<TangoSpaceAdapter.ViewHolder> {

    private final TangoSpacePresenter mPresenter;

    private LayoutInflater mInflater;

    public TangoSpaceAdapter(@NonNull TangoSpacePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }

        View itemView = mInflater.inflate(R.layout.list_item_tango_space_data, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements TangoSpaceItemView {

        @BindView(R.id.text_view_name)
        TextView mTextViewName;

        @BindView(R.id.text_view_uuid)
        TextView mTextViewUuid;

        private TangoSpacePresenter.TangoSpaceItemPresenter mItemPresenter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItemPresenter(@NonNull TangoSpacePresenter.TangoSpaceItemPresenter itemPresenter) {
            mItemPresenter = itemPresenter;
        }

        @Override
        public void showModel(@NonNull TangoSpaceItemModel model) {
            mTextViewName.setText(model.name);
            mTextViewUuid.setText(model.uuid);
        }

        public void onBind(@IntRange(from = 0) int position) {
            mItemPresenter.onBind(position);
        }

        @OnClick(R.id.button_export)
        void onClickExport() {
            mItemPresenter.onExport(getAdapterPosition());
        }
    }
}
