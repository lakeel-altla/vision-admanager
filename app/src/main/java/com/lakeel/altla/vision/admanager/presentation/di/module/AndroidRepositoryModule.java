package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.android.gms.common.api.GoogleApiClient;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.data.repository.android.DocumentBitmapRepository;
import com.lakeel.altla.vision.data.repository.android.DocumentRepository;
import com.lakeel.altla.vision.data.repository.android.PlaceRepository;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionIdRepository;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.data.repository.android.UserActorImageCacheRepository;
import com.lakeel.altla.vision.di.ActivityScope;

import android.content.ContentResolver;
import android.content.Context;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidRepositoryModule {

    @ActivityScope
    @Provides
    TangoAreaDescriptionMetadataRepository provideTangoAreaDescriptionMetadataRepository() {
        return new TangoAreaDescriptionMetadataRepository();
    }

    @ActivityScope
    @Provides
    TangoAreaDescriptionIdRepository provideTangoAreaDescriptionIdRepository() {
        return new TangoAreaDescriptionIdRepository();
    }

    @ActivityScope
    @Provides
    AreaDescriptionCacheRepository provideAreaDescriptionCacheRepository(
            @Named(Names.EXTERNAL_STORAGE_ROOT) File rootDirectory) {
        return new AreaDescriptionCacheRepository(rootDirectory);
    }

    @ActivityScope
    @Provides
    PlaceRepository providePlaceRepository(GoogleApiClient googleApiClient) {
        return new PlaceRepository(googleApiClient);
    }

    @ActivityScope
    @Provides
    DocumentRepository provideDocumentRepository(ContentResolver contentResolver) {
        return new DocumentRepository(contentResolver);
    }

    @ActivityScope
    @Provides
    DocumentBitmapRepository provideDocumentBitmapRepository(ContentResolver contentResolver) {
        return new DocumentBitmapRepository(contentResolver);
    }

    @ActivityScope
    @Provides
    UserActorImageCacheRepository provideUserActorImageCacheRepository(
            @Named(Names.ACTIVITY_CONTEXT) Context context) {
        return new UserActorImageCacheRepository(context);
    }
}
