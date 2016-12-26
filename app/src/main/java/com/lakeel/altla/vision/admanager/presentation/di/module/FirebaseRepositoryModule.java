package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserDeviceRepositoryImpl;
import com.lakeel.altla.vision.data.repository.firebase.UserProfileRepositoryImpl;
import com.lakeel.altla.vision.di.ActivityScope;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.repository.UserDeviceRepository;
import com.lakeel.altla.vision.domain.repository.UserProfileRepository;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseRepositoryModule {

    @ActivityScope
    @Provides
    public UserProfileRepository provideUserProfileRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserProfileRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public UserDeviceRepository provideUserDeviceRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserDeviceRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public UserAreaDescriptionRepository provideUserAreaDescriptionRepository(
            @Named(Names.FIREBASE_DATABASE_REFERENCE_ROOT) DatabaseReference reference) {
        return new UserAreaDescriptionRepositoryImpl(reference);
    }

    @ActivityScope
    @Provides
    public UserAreaDescriptionFileRepository provideUserAreaDescriptionFileRepository(
            @Named(Names.FIREBASE_STORAGE_REFERENCE_ROOT) StorageReference reference) {
        return new UserAreaDescriptionFileRepositoryImpl(reference);
    }
}
