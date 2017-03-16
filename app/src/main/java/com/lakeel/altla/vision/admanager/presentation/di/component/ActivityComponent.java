package com.lakeel.altla.vision.admanager.presentation.di.component;

import com.lakeel.altla.vision.admanager.presentation.di.ActivityScope;
import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.view.activity.MainActivity;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionByAreaListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaSelectFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserImageAssetEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserImageAssetFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserImageAssetListFragment;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = { ActivityModule.class })
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

    void inject(UserAreaDescriptionByAreaListFragment fragment);

    void inject(UserImageAssetListFragment fragment);

    void inject(UserImageAssetFragment fragment);

    void inject(UserImageAssetEditFragment fragment);
}
