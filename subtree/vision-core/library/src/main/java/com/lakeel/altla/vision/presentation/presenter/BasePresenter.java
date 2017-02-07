package com.lakeel.altla.vision.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BasePresenter<TView> implements Presenter<TView> {

    private final Log log = LogFactory.getLog(getClass());

    private final Handler handler = new Handler(Looper.getMainLooper());

    private TView view;

    private CompositeDisposable compositeDisposable;

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
    public final void onStart() {
        onStartOverride();
    }

    protected void onStartOverride() {
    }

    @Override
    public final void onStop() {
        if (compositeDisposable != null) compositeDisposable.clear();

        onStopOverride();
    }

    protected void onStopOverride() {
    }

    @Override
    public final void onResume() {
        onResumeOverride();
    }

    protected void onResumeOverride() {
    }

    @Override
    public final void onPause() {
        onPauseOverride();
    }

    protected void onPauseOverride() {
    }

    @NonNull
    protected final Log getLog() {
        return log;
    }

    @NonNull
    protected final TView getView() {
        return view;
    }

    protected final void manageDisposable(@NonNull Disposable disposable) {
        if (compositeDisposable == null) compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(disposable);
    }

    protected final void runOnUiThread(@NonNull Runnable runnable) {
        handler.post(runnable);
    }
}
