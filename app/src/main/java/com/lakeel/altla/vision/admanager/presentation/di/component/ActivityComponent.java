package com.lakeel.altla.vision.admanager.presentation.di.component;

import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.AndroidRepositoryModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.FirebaseRepositoryModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.GoogleApiModule;
import com.lakeel.altla.vision.admanager.presentation.view.activity.MainActivity;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.EditUserAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.EditUserAreaFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.SelectUserAreaFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.ShowUserAreaFragment;
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

    void inject(TangoAreaDescriptionListFragment fragment);

    void inject(UserAreaDescriptionListFragment fragment);

    void inject(EditUserAreaDescriptionFragment fragment);

    void inject(UserAreaListFragment fragment);

    void inject(ShowUserAreaFragment fragment);

    void inject(EditUserAreaFragment fragment);

    void inject(SelectUserAreaFragment fragment);
}
