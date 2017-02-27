package com.lakeel.altla.vision.admanager.presentation.view;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserAssetImageView {

    void onUpdateTitle(@Nullable String name);

    void onUpdateImageId(@NonNull String imageId);

    void onUpdateThumbnail(@NonNull Uri uri);

    void onUpdateName(@NonNull String name);

    void onUpdateCreatedAt(long createdAt);

    void onUpdateUpdatedAt(long updatedAt);

    void onShowUserActorImageEditView(String imageId);

    void onSnackbar(@StringRes int resId);
}
