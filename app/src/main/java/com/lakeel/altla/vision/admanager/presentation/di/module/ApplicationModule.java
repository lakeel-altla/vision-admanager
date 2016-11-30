package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final MyApplication application;

    public ApplicationModule(@NonNull MyApplication application) {
        this.application = application;
    }

    @Singleton
    @Provides
    public Resources provideResources() {
        return application.getResources();
    }
}
