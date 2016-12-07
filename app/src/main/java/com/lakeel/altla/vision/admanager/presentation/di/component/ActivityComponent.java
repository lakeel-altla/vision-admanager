package com.lakeel.altla.vision.admanager.presentation.di.component;

import com.lakeel.altla.vision.admanager.presentation.di.ActivityScope;
import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.AndroidRepositoryModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.FirebaseRepositoryModule;
import com.lakeel.altla.vision.admanager.presentation.view.activity.ManagerActivity;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.AppSpaceFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoSpaceFragment;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = { ActivityModule.class,
                          FirebaseRepositoryModule.class,
                          AndroidRepositoryModule.class })
public interface ActivityComponent {

    void inject(ManagerActivity activity);

    void inject(SignInFragment fragment);

    void inject(TangoPermissionFragment fragment);

    void inject(TangoSpaceFragment fragment);

    void inject(AppSpaceFragment fragment);
}
