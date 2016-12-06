package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.admanager.data.repository.firebase.AreaDescriptionEntryRepositoryImpl;
import com.lakeel.altla.vision.admanager.data.repository.firebase.AreaDescriptionFileRepositoryImpl;
import com.lakeel.altla.vision.admanager.domain.repository.AreaDescriptionEntryRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AreaDescriptionFileRepository;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseRepositoryModule {

    @ActivityScope
    @Provides
    public AreaDescriptionEntryRepository provideAreaDescriptionEntryRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_APP_ROOT) DatabaseReference reference, FirebaseAuth auth) {
        return new AreaDescriptionEntryRepositoryImpl(reference, auth);
    }

    @ActivityScope
    @Provides
    public AreaDescriptionFileRepository provideAreaDescriptionFileRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_APP_ROOT) StorageReference reference, FirebaseAuth auth) {
        return new AreaDescriptionFileRepositoryImpl(reference, auth);
    }
}
