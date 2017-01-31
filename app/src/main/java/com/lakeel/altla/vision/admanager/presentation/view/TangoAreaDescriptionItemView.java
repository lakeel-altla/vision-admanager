package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.TangoAreaDescriptionListPresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoAreaDescriptionItemModel;

import android.support.annotation.NonNull;

public interface TangoAreaDescriptionItemView {

    void setItemPresenter(@NonNull TangoAreaDescriptionListPresenter.TangoSpaceItemPresenter itemPresenter);

    void showModel(@NonNull TangoAreaDescriptionItemModel model);
}
