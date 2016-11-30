package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.vision.admanager.data.repository.android.AppContentRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.android.AppMetaDataRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.android.TangoMetaDataRepositoryImpl;
import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetaDataRepository;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScope;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidRepositoryModule {

    @ActivityScope
    @Provides
    public TangoMetaDataRepository provideTangoMetaDataRepository(Tango tango) {
        return new TangoMetaDataRepositoryImpl(tango);
    }

    @ActivityScope
    @Provides
    public AppContentRepository provideAppContentRepository(@Named(Names.LOCAL_DIRECTORY_CONTENT) File directory) {
        return new AppContentRepositoryImpl(directory);
    }

    @ActivityScope
    @Provides
    public AppMetaDataRepository provideAppMetaDataRepository(@Named(Names.LOCAL_DIRECTORY_META_DATA) File directory) {
        return new AppMetaDataRepositoryImpl(directory);
    }
}
