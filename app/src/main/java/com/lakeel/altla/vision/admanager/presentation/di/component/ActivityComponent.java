package com.lakeel.altla.vision.admanager.presentation.di.component;

import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.AndroidRepositoryModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.FirebaseRepositoryModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.GoogleApiModule;
import com.lakeel.altla.vision.admanager.presentation.view.activity.MainActivity;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.AppSpaceFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.EditUserAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoSpaceFragment;
import com.lakeel.altla.vision.di.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = { ActivityModule.class,
                          GoogleApiModule.class,
                          FirebaseRepositoryModule.class,
                          AndroidRepositoryModule.class })
public interface ActivityComponent {

    void inject(MainActivity activity);

    void inject(SignInFragment fragment);

    void inject(TangoPermissionFragment fragment);

    void inject(TangoSpaceFragment fragment);

    void inject(AppSpaceFragment fragment);

    void inject(EditUserAreaDescriptionFragment fragment);
}
