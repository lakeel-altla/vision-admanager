package com.lakeel.altla.vision.admanager.presentation.di.component;

import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.AndroidRepositoryModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.FirebaseRepositoryModule;
import com.lakeel.altla.vision.admanager.presentation.di.module.GoogleApiModule;
import com.lakeel.altla.vision.admanager.presentation.view.activity.MainActivity;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserActorImageListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionListInAreaFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaSelectFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserSceneEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserSceneFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserSceneListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserSceneListInAreaFragment;
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

    void inject(TangoAreaDescriptionFragment fragment);

    void inject(UserAreaDescriptionListFragment fragment);

    void inject(UserAreaDescriptionFragment fragment);

    void inject(UserAreaDescriptionEditFragment fragment);

    void inject(UserAreaListFragment fragment);

    void inject(UserAreaFragment fragment);

    void inject(UserAreaEditFragment fragment);

    void inject(UserAreaSelectFragment fragment);

    void inject(UserAreaDescriptionListInAreaFragment fragment);

    void inject(UserSceneListFragment fragment);

    void inject(UserSceneListInAreaFragment fragment);

    void inject(UserSceneFragment fragment);

    void inject(UserSceneEditFragment fragment);

    void inject(UserActorImageListFragment fragment);
}
