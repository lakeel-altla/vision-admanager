package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.ImportStatus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.io.File;

public interface UserAreaDescriptionView {

    void onUpdateTitle(@Nullable String name);

    void onUpdateAreaDescriptionId(@NonNull String areaDescriptionId);

    void onUpdateImportStatus(@NonNull ImportStatus importStatus);

    void onUpdateFileUploaded(boolean fileUploaded);

    void onUpdateFileCached(boolean fileCached);

    void onUpdateName(@Nullable String name);

    void onUpdateAreaName(@Nullable String areaName);

    void onUpdateCreatedAt(long createdAt);

    void onUpdateUpdatedAt(long updatedAt);

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

    void onBackView();

    void onSnackbar(@StringRes int resId);
}
