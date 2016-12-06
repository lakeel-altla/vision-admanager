package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.vision.admanager.presentation.di.ActivityScope;

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
    public Tango provideTango() {
        return new Tango(activity);
    }
}
