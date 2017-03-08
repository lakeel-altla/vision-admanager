package com.lakeel.altla.vision.admanager.presentation.view;

import android.net.Uri;
import android.support.annotation.Nullable;

public interface UserImageAssetItemView {

    void onUpdateName(@Nullable String name);

    void onUpdateThumbnail(@Nullable Uri uri);
}
