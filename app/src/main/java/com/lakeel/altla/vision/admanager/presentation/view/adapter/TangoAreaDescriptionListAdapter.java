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

public final class TangoAreaDescriptionListAdapter
        extends RecyclerView.Adapter<TangoAreaDescriptionListAdapter.ViewHolder> {

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

        View itemView = inflater.inflate(R.layout.item_tango_area_description, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements TangoAreaDescriptionItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewUuid;

        private TangoAreaDescriptionListPresenter.ItemPresenter itemPresenter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemPresenter = presenter.createItemPresenter();
            itemPresenter.onCreateItemView(this);
        }

        @Override
        public void showModel(@NonNull TangoAreaDescriptionItemModel model) {
            textViewName.setText(model.name);
            textViewUuid.setText(model.areaDescriptionId);
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
