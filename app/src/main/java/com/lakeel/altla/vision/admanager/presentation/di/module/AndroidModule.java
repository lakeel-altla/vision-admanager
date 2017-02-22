package com.lakeel.altla.vision.admanager.presentation.di.module;

import android.os.Environment;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidModule {

    @Named(Names.EXTERNAL_STORAGE_ROOT)
    @Singleton
    @Provides
    File provideExternalStorageRoot() {
        return Environment.getExternalStorageDirectory();
    }
}
