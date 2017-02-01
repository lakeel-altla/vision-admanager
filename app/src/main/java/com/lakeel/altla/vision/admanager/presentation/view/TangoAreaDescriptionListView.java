package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.io.File;

public interface TangoAreaDescriptionListView {

    void updateItems();

    void updateItem(int position);

    void updateItemRemoved(int position);

    void showSnackbar(@StringRes int resId);

    void showExportActivity(@NonNull String uuid, @NonNull File destinationDirectory);

    void showDeleteConfirmationDialog(int position);
}
