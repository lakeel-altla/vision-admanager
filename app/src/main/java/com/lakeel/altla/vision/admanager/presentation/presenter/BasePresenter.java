package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BasePresenter<TView> implements Presenter<TView> {

    private final Log log = LogFactory.getLog(getClass());

    private TView view;

    protected BasePresenter() {
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public final void onCreateView(@NonNull TView view) {
        this.view = view;

        onCreateViewOverride();
    }

    protected void onCreateViewOverride() {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @NonNull
    protected final Log getLog() {
        return log;
    }

    @NonNull
    protected final TView getView() {
        return view;
    }
}
