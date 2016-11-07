package com.lakeel.altla.vision.admanager.presentation.app;

import com.lakeel.altla.android.log.Log;
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

    private static final Log LOG = LogFactory.getLog(MyApplication.class);

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // LeakCanary
        LeakCanary.install(this);

        // Dagger 2
        mApplicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        // Altla Log
        LogFactory.setDebug(BuildConfig.DEBUG);
        LOG.i("Debug log enabled.");
    }

    public static ApplicationComponent getApplicationComponent(@NonNull Activity activity) {
        return ((MyApplication) activity.getApplication()).mApplicationComponent;
    }
}
