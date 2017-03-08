package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.StringRes;

public interface UserImageAssetListView {

    void onUpdateTitle(@StringRes int resId);

    void onItemInserted(int position);

    void onItemChanged(int position);

    void onItemRemoved(int position);

    void onItemMoved(int fromPosition, int toPosition);

    void onDataSetChanged();

    void onItemSelected(String imageId);

    void onSnackbar(@StringRes int resId);
}
