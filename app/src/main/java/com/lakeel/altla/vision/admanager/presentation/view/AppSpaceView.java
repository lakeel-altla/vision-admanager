package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.io.File;

public interface AppSpaceView {

    void updateItems();

    void updateItemRemoved(int position);

    void showSnackbar(@StringRes int resId);

    void showImportActivity(@NonNull File destinationFile);
}
