package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.lakeel.altla.vision.admanager.presentation.di.ServiceScope;
import com.lakeel.altla.vision.data.repository.android.DocumentRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetFileUploadTaskRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserImageAssetRepository;

import android.app.Service;
import android.content.ContentResolver;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public final class ServiceModule {

    private final Service service;

    public ServiceModule(@NonNull Service service) {
        this.service = service;
    }

    @ServiceScope
    @Provides
    ContentResolver provideContentResolver() {
        return service.getContentResolver();
    }

    @ServiceScope
    @Provides
    DocumentRepository provideDocumentRepository(ContentResolver contentResolver) {
        return new DocumentRepository(contentResolver);
    }

    @ServiceScope
    @Provides
    UserImageAssetFileRepository provideUserAssetImageFileRepository(FirebaseStorage storage) {
        return new UserImageAssetFileRepository(storage);
    }

    @ServiceScope
    @Provides
    UserImageAssetFileUploadTaskRepository provideUserAssetImageFileUploadTaskRepository(FirebaseDatabase database) {
        return new UserImageAssetFileUploadTaskRepository(database);
    }

    @ServiceScope
    @Provides
    UserImageAssetRepository provideUserAssetImageRepository(FirebaseDatabase database) {
        return new UserImageAssetRepository(database);
    }
}
