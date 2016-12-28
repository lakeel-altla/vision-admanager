package com.lakeel.altla.vision.admanager.presentation.di.module;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.di.ActivityScope;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import dagger.Module;
import dagger.Provides;

@Module
public final class GoogleApiModule {

    private static final Log LOG = LogFactory.getLog(GoogleApiModule.class);

    @ActivityScope
    @Provides
    public GoogleApiClient provideGoogleApiClient(AppCompatActivity activity, GoogleSignInOptions googleSignInOptions) {
        return new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, connectionResult -> {
                    LOG.e("Google API client connection error occured: %s", connectionResult);
                    Toast.makeText(activity, R.string.toast_google_api_client_connection_failed, Toast.LENGTH_LONG)
                         .show();
                })
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LOG.d("Google API client is connected.");
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        if (cause == CAUSE_NETWORK_LOST) {
                            LOG.d("Google API client connection suspended because of CAUSE_NETWORK_LOST.");
                        } else if (cause == CAUSE_SERVICE_DISCONNECTED) {
                            LOG.d("Google API client connection suspended because of CAUSE_SERVICE_DISCONNECTED.");
                        } else {
                            LOG.d("Google API client connection suspended because of an unknown cause: cause = %i",
                                  cause);
                        }
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }
}
