package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.StringRes;

public interface UserAreaListView {

    void updateItem(int position);

    void showSnackbar(@StringRes int resId);
}
