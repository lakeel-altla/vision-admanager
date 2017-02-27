package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.lakeel.altla.vision.data.repository.firebase.ConnectionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageFileUploadTaskRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAssetImageRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserConnectionRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserDeviceRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserProfileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserSceneRepository;
import com.lakeel.altla.vision.di.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public final class FirebaseRepositoryModule {

    @ActivityScope
    @Provides
    ConnectionRepository provideConnectionRepository(FirebaseDatabase database) {
        return new ConnectionRepository(database);
    }

    @ActivityScope
    @Provides
    UserConnectionRepository provideUserConnectionRepository(FirebaseDatabase database) {
        return new UserConnectionRepository(database);
    }

    @ActivityScope
    @Provides
    UserProfileRepository provideUserProfileRepository(FirebaseDatabase database) {
        return new UserProfileRepository(database);
    }

    @ActivityScope
    @Provides
    UserDeviceRepository provideUserDeviceRepository(FirebaseDatabase database) {
        return new UserDeviceRepository(database);
    }

    @ActivityScope
    @Provides
    UserAreaDescriptionRepository provideUserAreaDescriptionRepository(FirebaseDatabase database) {
        return new UserAreaDescriptionRepository(database);
    }

    @ActivityScope
    @Provides
    UserAreaDescriptionFileRepository provideUserAreaDescriptionFileRepository(FirebaseStorage storage) {
        return new UserAreaDescriptionFileRepository(storage);
    }

    @ActivityScope
    @Provides
    UserAreaRepository provideUserAreaRepository(FirebaseDatabase database) {
        return new UserAreaRepository(database);
    }

    @ActivityScope
    @Provides
    UserSceneRepository provideUserSceneRepository(FirebaseDatabase database) {
        return new UserSceneRepository(database);
    }

    @ActivityScope
    @Provides
    UserAssetImageRepository provideUserAssetImageRepository(FirebaseDatabase database) {
        return new UserAssetImageRepository(database);
    }

    @ActivityScope
    @Provides
    UserAssetImageFileRepository provideUserAssetImageFileRepository(FirebaseStorage storage) {
        return new UserAssetImageFileRepository(storage);
    }

    @ActivityScope
    @Provides
    UserAssetImageFileUploadTaskRepository provideUserAssetImageFileUploadTaskRepository(FirebaseDatabase database) {
        return new UserAssetImageFileUploadTaskRepository(database);
    }
}
