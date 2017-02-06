package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.io.File;

public interface UserAreaDescriptionListView {

    void onItemsUpdated();

    void onItemInserted(int position);

    void onItemSelected(@NonNull String areaDescriptionId);

    void onSnackbar(@StringRes int resId);

    void onShowImportActivity(@NonNull File destinationFile);

    void onShowUploadProgressDialog();

    void onUploadProgressUpdated(long max, long diff);

    void onHideUploadProgressDialog();
}
