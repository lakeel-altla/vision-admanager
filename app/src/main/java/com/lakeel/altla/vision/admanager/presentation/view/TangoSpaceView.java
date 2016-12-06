package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface TangoSpaceView {

    void updateItems();

    void updateItemRemoved(int position);

    void showSnackbar(@StringRes int resId);

    void showExportActivity(@NonNull String uuid, @NonNull String directory);
}
