package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.atap.tangoservice.Tango;
import com.google.firebase.auth.FirebaseAuth;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepositoryImpl;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepositoryImpl;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.di.ActivityScope;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidRepositoryModule {

    @ActivityScope
    @Provides
    public TangoAreaDescriptionMetadataRepository provideTangoMetadataRepository(Tango tango) {
        return new TangoAreaDescriptionMetadataRepositoryImpl(tango);
    }

    @ActivityScope
    @Provides
    public AreaDescriptionCacheRepository provideAppContentRepository(
            @Named(Names.EXTERNAL_STORAGE_ROOT) File rootDirectory, FirebaseAuth auth) {
        return new AreaDescriptionCacheRepositoryImpl(rootDirectory, auth);
    }
}
