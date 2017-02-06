package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.io.File;

public interface TangoAreaDescriptionListView {

    void onItemsUpdated();

    void onItemInserted(int position);

    void onItemRemoved(int position);

    void onItemSelected(@NonNull String areaDescriptionId);

    void onSnackbar(@StringRes int resId);

    void onShowTangoAreaDescriptionExportActivity(@NonNull String uuid, @NonNull File destinationDirectory);
}
