package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.io.File;

public interface TangoAreaDescriptionView {

    void onUpdateTitle(@Nullable String title);

    void onUpdateAreaDescriptionId(@Nullable String areaDescriptionId);

    void onUpdateExported(boolean exported);

    void onUpdateName(@Nullable String name);

    void onUpdateCreatedAt(long createdAt);

    void onUpdateActionExport(boolean enabled);

    void onShowTangoAreaDescriptionExportActivity(String areaDescriptionId, File directory);

    void onShowDeleteConfirmationDialog();

    void onBackView();

    void onSnackbar(@StringRes int resId);
}
