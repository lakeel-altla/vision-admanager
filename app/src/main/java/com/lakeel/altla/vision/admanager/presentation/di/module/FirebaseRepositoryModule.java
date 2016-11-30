package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.lakeel.altla.vision.admanager.data.repository.firebase.FirebaseContentRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.firebase.FirebaseMetaDataRepositoryImpl;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseMetaDataRepository;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseRepositoryModule {

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
