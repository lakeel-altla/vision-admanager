package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.vision.admanager.data.repository.android.AppContentRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.android.AppMetadataRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.android.TangoMetadataRepositoryImpl;
import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetadataRepository;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetadataRepository;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScope;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidRepositoryModule {

    @ActivityScope
    @Provides
    public TangoMetadataRepository provideTangoMetadataRepository(Tango tango) {
        return new TangoMetadataRepositoryImpl(tango);
    }

    @ActivityScope
    @Provides
    public AppContentRepository provideAppContentRepository(@Named(Names.LOCAL_DIRECTORY_CONTENT) File directory) {
        return new AppContentRepositoryImpl(directory);
    }

    @ActivityScope
    @Provides
    public AppMetadataRepository provideAppMetadataRepository(@Named(Names.LOCAL_DIRECTORY_METADATA) File directory) {
        return new AppMetadataRepositoryImpl(directory);
    }
}
