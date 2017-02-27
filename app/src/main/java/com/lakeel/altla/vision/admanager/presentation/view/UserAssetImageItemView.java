package com.lakeel.altla.vision.admanager.presentation.view;

import android.net.Uri;
import android.support.annotation.NonNull;

public interface UserAssetImageItemView {

    void onUpdateName(@NonNull String name);

    void onUpdateThumbnail(@NonNull Uri uri);
}
