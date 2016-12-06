package com.lakeel.altla.vision.admanager.presentation.di.module;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class ConfigModule {

    private static final String FIREBASE_STORAGE_URI = "gs://firebase-trial.appspot.com";

    @Named(Names.FIREBASE_STORAGE_URI)
    @Singleton
    @Provides
    public String provideFirebaseStorageUri() {
        return FIREBASE_STORAGE_URI;
    }
}
