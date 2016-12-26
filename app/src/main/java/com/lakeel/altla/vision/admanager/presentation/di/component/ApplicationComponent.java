package com.lakeel.altla.vision.admanager.presentation.di.component;

import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.ApplicationModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.ConfigModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.ExternalStorageModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.FirebaseDatabaseModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.FirebaseStorageModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.GoogleSignInModule;

import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class,
                       ConfigModule.class,
                       ExternalStorageModule.class,
                       GoogleSignInModule.class,
                       FirebaseDatabaseModule.class,
                       FirebaseStorageModule.class })
public interface ApplicationComponent {

    ActivityComponent activityComponent(ActivityModule module);

    Resources resources();
}
