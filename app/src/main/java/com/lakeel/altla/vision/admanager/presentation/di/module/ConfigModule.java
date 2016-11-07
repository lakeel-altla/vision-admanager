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

    private static final String LOCAL_DIRECTORY_META_DATA = "AreaDescriptionMetaData";

    private static final String FIREBASE_STORAGE_URI = "gs://firebase-trial.appspot.com";

    private static final String FIREBASE_STORAGE_PATH_CONTENT = "areaDescriptions";

    private static final String FIREBASE_DATABASE_NODE_META_DATA = "areaDescriptionMetaDatas";

    @Named(Names.LOCAL_DIRECTORY_CONTENT)
    @Singleton
    @Provides
    public File provideContentDirectory() {
        return new File(Environment.getExternalStorageDirectory(), LOCAL_DIRECTORY_CONTENT);
    }

    @Named(Names.LOCAL_DIRECTORY_META_DATA)
    @Singleton
    @Provides
    public File provideMetaDataDirectory() {
        return new File(Environment.getExternalStorageDirectory(), LOCAL_DIRECTORY_META_DATA);
    }

    @Named(Names.FIREBASE_STORAGE_URI)
    @Singleton
    @Provides
    public String provideFirebaseStorageUri() {
        return FIREBASE_STORAGE_URI;
    }

    @Named(Names.FIREBASE_STORAGE_PATH_CONTENT)
    @Singleton
    @Provides
    public String provideFirebaseStorageContentPath() {
        return FIREBASE_STORAGE_PATH_CONTENT;
    }

    @Named(Names.FIREBASE_DATABASE_NODE_META_DATA)
    @Singleton
    @Provides
    public String provideFirebaseDatabaseNodeMetaData() {
        return FIREBASE_DATABASE_NODE_META_DATA;
    }
}
