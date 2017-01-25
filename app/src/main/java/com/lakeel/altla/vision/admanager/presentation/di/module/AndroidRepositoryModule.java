package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.android.gms.common.api.GoogleApiClient;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.data.repository.android.PlaceRepository;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.di.ActivityScope;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidRepositoryModule {

    @ActivityScope
    @Provides
    public TangoAreaDescriptionMetadataRepository provideTangoAreaDescriptionMetadataRepository() {
        return new TangoAreaDescriptionMetadataRepository();
    }

    @ActivityScope
    @Provides
    public AreaDescriptionCacheRepository provideAreaDescriptionCacheRepository(
            @Named(Names.EXTERNAL_STORAGE_ROOT) File rootDirectory) {
        return new AreaDescriptionCacheRepository(rootDirectory);
    }

    @ActivityScope
    @Provides
    public PlaceRepository providePlaceRepository(GoogleApiClient googleApiClient) {
        return new PlaceRepository(googleApiClient);
    }
}
