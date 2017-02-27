package com.lakeel.altla.vision.admanager.presentation.di.component;

import com.lakeel.altla.vision.admanager.presentation.di.ServiceScope;
import com.lakeel.altla.vision.admanager.presentation.di.module.ServiceModule;
import com.lakeel.altla.vision.admanager.presentation.service.UserAssetImageFileUploadTaskService;

import android.support.annotation.NonNull;

import dagger.Subcomponent;

@ServiceScope
@Subcomponent(modules = { ServiceModule.class })
public interface ServiceComponent {

    void inject(@NonNull UserAssetImageFileUploadTaskService service);
}
