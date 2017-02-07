package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoAreaDescriptionModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.io.File;

public interface TangoAreaDescriptionView {

    void onModelUpdated(@NonNull TangoAreaDescriptionModel model);

    void onUpdateActionExport(boolean enabled);

    void onShowTangoAreaDescriptionExportActivity(String areaDescriptionId, File directory);

    void onShowDeleteConfirmationDialog();

    void onDeleted();

    void onSnackbar(@StringRes int resId);
}
