package com.lakeel.altla.vision.admanager.presentation.view.adapter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaItemView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserAreaListAdapter extends RecyclerView.Adapter<UserAreaListAdapter.ViewHolder> {

    private final ItemProvider<UserAreaItemView> itemProvider;

    private LayoutInflater inflater;

    public UserAreaListAdapter(@NonNull ItemProvider<UserAreaItemView> itemProvider) {
        this.itemProvider = itemProvider;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = inflater.inflate(R.layout.item_select_user_area, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        itemProvider.onBind(position, holder);
    }

    @Override
    public int getItemCount() {
        return itemProvider.getItemCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements UserAreaItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewId;

        @BindView(R.id.text_view_place_name)
        TextView textViewPlaceName;

        @BindView(R.id.text_view_place_address)
        TextView textViewPlaceAddress;

        @BindView(R.id.text_view_level)
        TextView textViewLevel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void showModel(@NonNull UserAreaItemModel model) {
            textViewName.setText(model.name);
            textViewId.setText(model.areaId);
            textViewPlaceName.setText(model.placeName);
            textViewPlaceAddress.setText(model.placeAddress);
            textViewLevel.setText(model.level);
        }
    }
}
