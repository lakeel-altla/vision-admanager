package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.lakeel.altla.vision.admanager.presentation.di.ServiceScope;
import com.lakeel.altla.vision.data.repository.android.DocumentRepository;
import com.lakeel.altla.vision.data.repository.firebase.UploadUserActorImageFileTaskRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserActorImageFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserActorImageRepository;

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
    UserActorImageFileRepository provideUserActorImageFileRepository(FirebaseStorage storage) {
        return new UserActorImageFileRepository(storage);
    }

    @ServiceScope
    @Provides
    UploadUserActorImageFileTaskRepository provideUploadUserActorImageFileTaskRepository(FirebaseDatabase database) {
        return new UploadUserActorImageFileTaskRepository(database);
    }

    @ServiceScope
    @Provides
    UserActorImageRepository provideUserActorImageRepository(FirebaseDatabase database) {
        return new UserActorImageRepository(database);
    }
}
