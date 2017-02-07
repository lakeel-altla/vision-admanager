package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface UserAreaDescriptionListInAreaView {

    void onItemInserted(int position);

    void onItemsUpdated();

    void onItemSelected(@NonNull String areaDescriptionId);

    void onUpdateTitle(String title);

    void onSnackbar(@StringRes int resId);
}
