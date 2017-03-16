package com.lakeel.altla.vision.admanager.presentation.di.component;

import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.ApplicationModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.ServiceModule;

import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class })
public interface ApplicationComponent {

    ActivityComponent activityComponent(ActivityModule module);

    ServiceComponent serviceComponent(ServiceModule module);

    Resources resources();
}
