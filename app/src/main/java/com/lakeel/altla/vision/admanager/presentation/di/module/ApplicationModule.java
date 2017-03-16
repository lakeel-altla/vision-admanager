package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final MyApplication application;

    public ApplicationModule(@NonNull MyApplication application) {
        this.application = application;
    }

    @Named(Names.APPLICATION_CONTEXT)
    @Singleton
    @Provides
    Context provideContext() {
        return application;
    }

    @Singleton
    @Provides
    Resources provideResources() {
        return application.getResources();
    }

    @Named(Names.GOOGLE_API_WEB_CLIENT_ID)
    @Singleton
    @Provides
    int provideGoogleApiWebClientId() {
        return R.string.default_web_client_id;
    }

    @Singleton
    @Provides
    GoogleSignInOptions provideGoogleSignInOptions(Resources resources,
                                                   @Named(Names.GOOGLE_API_WEB_CLIENT_ID) int webClientId) {
        return new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(webClientId))
                .requestEmail()
                .requestProfile()
                .build();

    }

    @Singleton
    @Provides
    FirebaseDatabase provideFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    @Singleton
    @Provides
    FirebaseStorage provideFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }
}
