package com.lakeel.altla.vision.admanager.presentation.view;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserActorImageEditView {

    void onUpdateViewsEnabled(boolean enabled);

    void onUpdateActionSave(boolean enabled);

    void onUpdateHomeAsUpIndicator(@DrawableRes int resId);

    void onUpdateHomeAsUpIndicator(@Nullable Drawable drawable);

    void onUpdateTitle(@Nullable String title);

    void onUpdateThumbnail(@NonNull Uri uri);

    void onUpdateName(@Nullable String name);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onShowImagePicker();

    void onBackView();

    void onSnackbar(@StringRes int resId);
}
