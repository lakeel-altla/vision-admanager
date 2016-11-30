package com.lakeel.altla.vision.admanager.presentation.di.component;

import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.ApplicationModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.ConfigModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.FirebaseAuthModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.FirebaseDatabaseModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.FirebaseStorageModule;

import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class,
                       ConfigModule.class,
                       FirebaseAuthModule.class,
                       FirebaseDatabaseModule.class,
                       FirebaseStorageModule.class })
public interface ApplicationComponent {

    UserComponent userComponent(ActivityModule module);

    Resources resources();
}
