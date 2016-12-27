package com.lakeel.altla.vision.admanager.presentation.view;

import com.lakeel.altla.vision.admanager.presentation.presenter.TangoSpacePresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;

import android.support.annotation.NonNull;

public interface TangoSpaceItemView {

    void setItemPresenter(@NonNull TangoSpacePresenter.TangoSpaceItemPresenter itemPresenter);

    void showModel(@NonNull TangoSpaceItemModel model);
}
