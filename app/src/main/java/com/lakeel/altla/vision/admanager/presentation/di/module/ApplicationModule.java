package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.admanager.BuildConfig;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;
import com.lakeel.altla.vision.domain.helper.CurrentApplicationResolver;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final MyApplication application;

    public ApplicationModule(@NonNull MyApplication application) {
        this.application = application;
    }

    @Named(Names.APPLICATION_CONTEXT)
    @Singleton
    @Provides
    Context provideContext() {
        return application;
    }

    @Singleton
    @Provides
    Resources provideResources() {
        return application.getResources();
    }

    @Singleton
    @Provides
    CurrentApplicationResolver provideCurrentApplicationResolver() {
        return new CurrentApplicationResolver(BuildConfig.APPLICATION_ID);
    }

    @Singleton
    @Provides
    CurrentDeviceResolver provideCurrentDeviceResolver() {
        return new CurrentDeviceResolver(FirebaseInstanceId.getInstance().getId());
    }
}
