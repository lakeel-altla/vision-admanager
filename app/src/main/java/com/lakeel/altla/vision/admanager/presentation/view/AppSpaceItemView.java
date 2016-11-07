package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.AppSpacePresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;

import android.support.annotation.NonNull;

public interface AppSpaceItemView {

    void setItemPresenter(@NonNull AppSpacePresenter.AppSpaceItemPresenter appSpaceItemPresenter);

    void showModel(@NonNull AppSpaceItemModel model);
}
