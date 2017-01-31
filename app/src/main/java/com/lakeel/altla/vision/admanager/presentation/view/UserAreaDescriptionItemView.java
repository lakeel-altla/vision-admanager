package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.UserAreaDescriptionListPresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionItemModel;

import android.support.annotation.NonNull;

public interface UserAreaDescriptionItemView {

    void setItemPresenter(@NonNull UserAreaDescriptionListPresenter.AppSpaceItemPresenter appSpaceItemPresenter);

    void showModel(@NonNull UserAreaDescriptionItemModel model);
}
