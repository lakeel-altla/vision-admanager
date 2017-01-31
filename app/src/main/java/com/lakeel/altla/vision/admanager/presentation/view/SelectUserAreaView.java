package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.StringRes;

public interface SelectUserAreaView {

    void updateItem(int position);

    void updateItems();

    void onItemSelected(String areaId);

    void showSnackbar(@StringRes int resId);
}
