package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.admanager.data.repository.firebase.FirebaseContentRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.firebase.FirebaseMetadataRepositoryImpl;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseMetadataRepository;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseRepositoryModule {

    @ActivityScope
    @Provides
    public FirebaseContentRepository provideFirebaseContentRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_APP_ROOT) StorageReference reference, FirebaseAuth auth) {
        return new FirebaseContentRepositoryImpl(reference, auth);
    }

    @ActivityScope
    @Provides
    public FirebaseMetadataRepository provideFirebaseMetadataRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_APP_ROOT) DatabaseReference reference, FirebaseAuth auth) {
        return new FirebaseMetadataRepositoryImpl(reference, auth);
    }
}
