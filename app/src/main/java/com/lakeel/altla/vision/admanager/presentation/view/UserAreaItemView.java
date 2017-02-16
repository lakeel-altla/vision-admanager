package com.lakeel.altla.vision.admanager.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface UserAreaItemView {

    void onUpdateAreaId(@NonNull String areaId);

    void onUpdateName(@NonNull String name);

    void onUpdatePlaceName(@Nullable String placeName);

    void onUpdatePladeAddress(@Nullable String placeAddress);

    void onUpdateLevel(int level);
}
