package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.vision.admanager.data.repository.AppContentRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.AppMetaDataRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.FirebaseContentRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.FirebaseMetaDataRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.TangoMetaDataRepositoryImpl;
import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetaDataRepository;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseMetaDataRepository;
import com.lakeel.altla.vision.admanager.domain.repository.TangoMetaDataRepository;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScope;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class RepositoryModule {

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

    @ActivityScope
    @Provides
    public FirebaseContentRepository provideCloudContentRepository(
            @Named(Names.FIREBASE_STORAGE_URI) String uri,
            @Named(Names.FIREBASE_STORAGE_PATH_CONTENT) String contentPath) {
        return new FirebaseContentRepositoryImpl(uri, contentPath);
    }

    @ActivityScope
    @Provides
    public FirebaseMetaDataRepository provideCloudMetaDataRepository(
            @Named(Names.FIREBASE_DATABASE_NODE_META_DATA) String metaDataNode) {
        return new FirebaseMetaDataRepositoryImpl(metaDataNode);
    }
}
