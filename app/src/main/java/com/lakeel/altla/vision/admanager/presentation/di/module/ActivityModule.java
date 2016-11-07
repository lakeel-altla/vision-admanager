package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.vision.admanager.presentation.di.ActivityScope;

import android.app.Activity;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public final class ActivityModule {

    private final Activity mActivity;

    public ActivityModule(@NonNull Activity activity) {
        mActivity = activity;
    }

    @ActivityScope
    @Provides
    public Tango provideTango() {
        return new Tango(mActivity);
    }
}
