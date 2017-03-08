package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserAreaView {

    void onUpdateTitle(@Nullable String title);

    void onUpdateAreaId(@NonNull String areaId);

    void onUpdateName(@Nullable String name);

    void onUpdatePlaceName(@Nullable String placeName);

    void onUpdatePlaceAddress(@Nullable String placeAddress);

    void onUpdateLevel(int level);

    void onUpdateCreatedAt(long createdAt);

    void onUpdateUpdatedAt(long updatedAt);

    void onShowUserAreaEditView(@NonNull String areaId);

    void onShowUserAreaDescriptionListInAreaView(@NonNull String areaId);

    void onShowUserSceneListInAreaView(@NonNull String areaId);

    void onSnackbar(@StringRes int resId);
}
