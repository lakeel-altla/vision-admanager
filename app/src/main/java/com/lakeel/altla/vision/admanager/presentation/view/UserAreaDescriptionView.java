package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.io.File;

public interface UserAreaDescriptionView {

    void onModelUpdated(@NonNull UserAreaDescriptionModel model);

    void onShowImportActivity(File file);

    void onShowProgressDialog(@StringRes int messageResId);

    void onProgressUpdated(long totalBytes, long increment);

    void onHideProgressDialog();

    void onUpdateActionImport(boolean enabled);

    void onUpdateActionUpload(boolean enabled);

    void onUpdateActionDownload(boolean enabled);

    void onUpdateActionDeleteCache(boolean enabled);

    void onShowUserAreaDescriptionEditView(@NonNull String areaDescriptionId);

    void onShowDeleteConfirmationDialog();

    void onDeleted();

    void onSnackbar(@StringRes int resId);
}
