package com.lakeel.altla.vision.admanager.presentation.di.module;

import android.os.Environment;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class ConfigModule {

    private static final String LOCAL_DIRECTORY_CONTENT = "AreaDescription";

    private static final String LOCAL_DIRECTORY_METADATA = "AreaDescriptionMetadata";

    private static final String FIREBASE_STORAGE_URI = "gs://firebase-trial.appspot.com";

    @Named(Names.LOCAL_DIRECTORY_CONTENT)
    @Singleton
    @Provides
    public File provideContentDirectory() {
        return new File(Environment.getExternalStorageDirectory(), LOCAL_DIRECTORY_CONTENT);
    }

    @Named(Names.LOCAL_DIRECTORY_METADATA)
    @Singleton
    @Provides
    public File provideMetadataDirectory() {
        return new File(Environment.getExternalStorageDirectory(), LOCAL_DIRECTORY_METADATA);
    }

    @Named(Names.FIREBASE_STORAGE_URI)
    @Singleton
    @Provides
    public String provideFirebaseStorageUri() {
        return FIREBASE_STORAGE_URI;
    }
}
