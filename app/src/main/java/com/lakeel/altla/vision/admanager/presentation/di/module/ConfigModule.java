package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.lakeel.altla.vision.admanager.R;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class ConfigModule {

    @Named(Names.GOOGLE_SIGN_IN_WEB_CLIENT_ID)
    @Singleton
    @Provides
    public int provideGoogleSignInWebClientId() {
        return R.string.default_web_client_id;
    }
}
