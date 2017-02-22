package com.lakeel.altla.vision.admanager.presentation.view;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public interface UserActorImageItemView {

    void onUpdateName(@NonNull String name);

    void onUpdateThumbnail(@NonNull Bitmap bitmap);

    void onUpdateProgressRingThumbnailVisible(boolean visible);
}
