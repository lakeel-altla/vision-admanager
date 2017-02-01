package com.lakeel.altla.vision.admanager.presentation.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Presenter<TView> {

    void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState);

    void onCreateView(@NonNull TView view);

    void onStart();

    void onStop();

    void onResume();

    void onPause();
}
