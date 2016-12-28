package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.io.File;

public interface AppSpaceView {

    void updateItems();

    void updateItem(int position);

    void updateItemRemoved(int position);

    void showSnackbar(@StringRes int resId);

    void showImportActivity(@NonNull File destinationFile);

    void showUploadProgressDialog();

    void setUploadProgressDialogProgress(long max, long diff);

    void hideUploadProgressDialog();

    void showDeleteProgressDialog();

    void hideDeleteProgressDialog();

    void showDeleteConfirmationDialog(int position);

    void showEditUserAreaDescriptionFragment(String areaDescriptionId);
}
