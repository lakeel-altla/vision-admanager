package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserDeviceRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserProfileRepository;
import com.lakeel.altla.vision.di.ActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseRepositoryModule {

    @ActivityScope
    @Provides
    public UserProfileRepository provideUserProfileRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserProfileRepository(reference);
    }

    @ActivityScope
    @Provides
    public UserDeviceRepository provideUserDeviceRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserDeviceRepository(reference);
    }

    @ActivityScope
    @Provides
    public UserAreaDescriptionRepository provideUserAreaDescriptionRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserAreaDescriptionRepository(reference);
    }

    @ActivityScope
    @Provides
    public UserAreaDescriptionFileRepository provideUserAreaDescriptionFileRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_ROOT) StorageReference reference) {
        return new UserAreaDescriptionFileRepository(reference);
    }
}
