package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoAreaDescriptionModel;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface TangoAreaDescriptionView {

    void onModelUpdated(@NonNull TangoAreaDescriptionModel model);

    void onShowTangoAreaDescriptionEditView(@NonNull String areaDescriptionId);

    void onShowDeleteConfirmationDialog();

    void onDeleted();

    void onSnackbar(@StringRes int resId);
}
