package com.lakeel.altla.vision.admanager.presentation.view.adapter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.TangoAreaDescriptionListPresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoAreaDescriptionItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionItemView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class TangoAreaDescriptionListAdapter extends RecyclerView.Adapter<TangoAreaDescriptionListAdapter.ViewHolder> {

    private final TangoAreaDescriptionListPresenter presenter;

    private LayoutInflater inflater;

    public TangoAreaDescriptionListAdapter(@NonNull TangoAreaDescriptionListPresenter presenter) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements TangoAreaDescriptionItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewUuid;

        private TangoAreaDescriptionListPresenter.TangoSpaceItemPresenter itemPresenter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItemPresenter(@NonNull TangoAreaDescriptionListPresenter.TangoSpaceItemPresenter itemPresenter) {
            this.itemPresenter = itemPresenter;
        }

        @Override
        public void showModel(@NonNull TangoAreaDescriptionItemModel model) {
            textViewName.setText(model.name);
            textViewUuid.setText(model.areaDescriptionId);
        }

        public void onBind(int position) {
            itemPresenter.onBind(position);
        }

        @OnClick(R.id.image_button_export)
        void onClickImageButtonExport() {
            itemPresenter.onClickImageButtonExport(getAdapterPosition());
        }

        @OnClick(R.id.image_button_delete)
        void onClickImageButtonDelete() {
            itemPresenter.onClickImageButtonDelete(getAdapterPosition());
        }
    }
}
