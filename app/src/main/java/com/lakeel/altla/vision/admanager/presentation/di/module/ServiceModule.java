package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.lakeel.altla.vision.admanager.presentation.di.ServiceScope;
import com.lakeel.altla.vision.api.VisionService;

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
    VisionService provideVisionService(FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage) {
        return new VisionService(service, firebaseDatabase, firebaseStorage);
    }
}
