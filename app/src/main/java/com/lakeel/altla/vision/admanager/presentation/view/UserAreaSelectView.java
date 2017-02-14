package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface UserAreaSelectView {

    void onItemInserted(int position);

    void onItemsUpdated();

    void onItemSelected(@NonNull String areaId);

    void onSnackbar(@StringRes int resId);
}
