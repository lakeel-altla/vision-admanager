package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.StringRes;

public interface UserSceneListView {

    void onUpdateTitle(@StringRes int resId);

    void onItemInserted(int position);

    void onItemsUpdated();

    void onItemSelected(String sceneId);

    void onSnackbar(@StringRes int resId);
}
