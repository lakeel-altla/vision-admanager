package com.lakeel.altla.vision.admanager.presentation.app;

import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.BuildConfig;
import com.lakeel.altla.vision.admanager.presentation.di.component.ApplicationComponent;
import com.lakeel.altla.vision.admanager.presentation.di.component.DaggerApplicationComponent;
import com.lakeel.altla.vision.admanager.presentation.di.module.ApplicationModule;
import com.squareup.leakcanary.LeakCanary;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

public final class MyApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // LeakCanary
        LeakCanary.install(this);

        // Dagger 2
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        // Altla Log
        LogFactory.setDebug(BuildConfig.DEBUG);

        // Enable the offline feature of Firebase Database.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static ApplicationComponent getApplicationComponent(@NonNull Activity activity) {
        return ((MyApplication) activity.getApplication()).applicationComponent;
    }
}
