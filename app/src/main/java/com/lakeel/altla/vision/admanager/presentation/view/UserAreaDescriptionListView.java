package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.io.File;

public interface UserAreaDescriptionListView {

    void onItemsUpdated();

    void onItemInserted(int position);

    void onItemRemoved(int position);

    void onSnackbar(@StringRes int resId);

    void onShowImportActivity(@NonNull File destinationFile);

    void onShowUploadProgressDialog();

    void onUploadProgressUpdated(long max, long diff);

    void onHideUploadProgressDialog();

    void onShowDeleteProgressDialog();

    void onHideDeleteProgressDialog();

    void onShowDeleteConfirmationDialog(int position);

    void onShowUserAreaDescriptionEditView(String areaDescriptionId);
}
