package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.lakeel.altla.vision.data.repository.firebase.ConnectionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserConnectionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserDeviceRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserProfileRepository;
import com.lakeel.altla.vision.di.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseRepositoryModule {

    @ActivityScope
    @Provides
    public ConnectionRepository provideConnectionRepository(FirebaseDatabase database) {
        return new ConnectionRepository(database);
    }

    @ActivityScope
    @Provides
    public UserConnectionRepository provideUserConnectionRepository(FirebaseDatabase database) {
        return new UserConnectionRepository(database);
    }

    @ActivityScope
    @Provides
    public UserProfileRepository provideUserProfileRepository(FirebaseDatabase database) {
        return new UserProfileRepository(database);
    }

    @ActivityScope
    @Provides
    public UserDeviceRepository provideUserDeviceRepository(FirebaseDatabase database) {
        return new UserDeviceRepository(database);
    }

    @ActivityScope
    @Provides
    public UserAreaDescriptionRepository provideUserAreaDescriptionRepository(FirebaseDatabase database) {
        return new UserAreaDescriptionRepository(database);
    }

    @ActivityScope
    @Provides
    public UserAreaDescriptionFileRepository provideUserAreaDescriptionFileRepository(FirebaseStorage storage) {
        return new UserAreaDescriptionFileRepository(storage);
    }

    @ActivityScope
    @Provides
    public UserAreaRepository provideUserAreaRepository(FirebaseDatabase database) {
        return new UserAreaRepository(database);
    }
}
