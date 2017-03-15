package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserAreaDescriptionByAreaListView {

    void onUpdateTitle(@Nullable String title);

    void onItemInserted(int position);

    void onItemChanged(int position);

    void onItemRemoved(int position);

    void onItemMoved(int fromPosition, int toPosition);

    void onDataSetChanged();

    void onItemSelected(@NonNull String areaDescriptionId);

    void onSnackbar(@StringRes int resId);
}
