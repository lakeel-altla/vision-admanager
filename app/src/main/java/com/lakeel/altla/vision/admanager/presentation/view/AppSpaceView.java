package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface AppSpaceView {

    void updateItems();

    void updateItemRemoved(@IntRange(from = 0) int position);

    void showSnackbar(@StringRes int resId);

    void showImportActivity(@NonNull String path);

    void showUploadProgressDialog();

    void setUploadProgressDialogProgress(long max, long diff);

    void hideUploadProgressDialog();
}
