package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepositoryImpl;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepositoryImpl;
import com.lakeel.altla.vision.di.ActivityScope;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidRepositoryModule {

    @ActivityScope
    @Provides
    public TangoAreaDescriptionMetadataRepository provideTangoMetadataRepository() {
        return new TangoAreaDescriptionMetadataRepositoryImpl();
    }

    @ActivityScope
    @Provides
    public AreaDescriptionCacheRepository provideAreaDescriptionCacheRepository(
            @Named(Names.EXTERNAL_STORAGE_ROOT) File rootDirectory) {
        return new AreaDescriptionCacheRepositoryImpl(rootDirectory);
    }
}
