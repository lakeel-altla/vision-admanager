package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserSceneListInAreaView {

    void onUpdateTitle(@Nullable String title);

    void onItemInserted(int position);

    void onItemsUpdated();

    void onItemSelected(String sceneId);

    void onSnackbar(@StringRes int resId);
}
