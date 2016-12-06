package com.lakeel.altla.vision.admanager.presentation.view.adapter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.TangoSpacePresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceItemView;

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

    private final TangoSpacePresenter presenter;

    private LayoutInflater inflater;

    public TangoSpaceAdapter(@NonNull TangoSpacePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        View itemView = inflater.inflate(R.layout.item_tango_space_model, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements TangoSpaceItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewUuid;

        private TangoSpacePresenter.TangoSpaceItemPresenter itemPresenter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItemPresenter(@NonNull TangoSpacePresenter.TangoSpaceItemPresenter itemPresenter) {
            this.itemPresenter = itemPresenter;
        }

        @Override
        public void showModel(@NonNull TangoSpaceItemModel model) {
            textViewName.setText(model.name);
            textViewUuid.setText(model.id);
        }

        public void onBind(int position) {
            itemPresenter.onBind(position);
        }

        @OnClick(R.id.button_export)
        void onClickExport() {
            itemPresenter.onExport(getAdapterPosition());
        }
    }
}
