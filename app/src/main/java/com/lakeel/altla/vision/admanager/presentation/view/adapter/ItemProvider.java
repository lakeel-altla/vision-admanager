package com.lakeel.altla.vision.admanager.presentation.view.adapter;

public interface ItemProvider<TItemView> {

    int getItemCount();

    void onBind(int position, TItemView itemView);
}
