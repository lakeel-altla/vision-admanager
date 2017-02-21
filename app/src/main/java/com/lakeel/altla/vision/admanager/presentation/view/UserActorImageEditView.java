package com.lakeel.altla.vision.admanager.presentation.view;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserActorImageEditView {

    void onUpdateViewsEnabled(boolean enabled);

    void onUpdateActionSave(boolean enabled);

    void onUpdateHomeAsUpIndicator(@DrawableRes int resId);

    void onUpdateHomeAsUpIndicator(@Nullable Drawable drawable);

    void onUpdateTitle(@Nullable String title);

    void onUpdateThumbnail(@Nullable Bitmap bitmap);

    void onUpdateName(@Nullable String name);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onUpdateProgressBarThumbnailVisible(boolean visible);

    void onShowImagePicker();

    void onBackView();

    void onSnackbar(@StringRes int resId);
}
