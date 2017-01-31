package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface UserAreaListView {

    void updateItem(int position);

    void updateItems();

    void showEditItemView(@NonNull String areaId);

    void showSnackbar(@StringRes int resId);
}
