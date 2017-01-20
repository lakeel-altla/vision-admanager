package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.atap.tangoservice.TangoConfig;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.di.ActivityScope;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class ActivityModule {

    private final AppCompatActivity activity;

    public ActivityModule(@NonNull AppCompatActivity activity) {
        this.activity = activity;
    }

    @ActivityScope
    @Provides
    public AppCompatActivity provideActivity() {
        return activity;
    }

    @Named(Names.ACTIVITY_CONTEXT)
    @ActivityScope
    @Provides
    public Context provideContext() {
        return activity;
    }

    @ActivityScope
    @Provides
    public TangoWrapper provideTangoWrapper(@Named(Names.ACTIVITY_CONTEXT) Context context) {
        TangoWrapper tangoWrapper = new TangoWrapper(context);
        tangoWrapper.setTangoConfigFactory(tango -> tango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT));
        return tangoWrapper;
    }
}
