package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserAreaModelMapper;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaView;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaPresenter extends BasePresenter<UserAreaView> {

    private static final String ARG_AREA_ID = "areaId";

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    private String areaId;

    @Inject
    public UserAreaPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull String areaId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_ID, areaId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String areaId = arguments.getString(ARG_AREA_ID, null);
        if (areaId == null) {
            throw new IllegalArgumentException(String.format("Argument '%s' must be not null.", ARG_AREA_ID));
        }

        this.areaId = areaId;
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        Disposable disposable = findUserAreaUseCase
                .execute(areaId)
                .map(UserAreaModelMapper::map)
                .flatMapObservable(model -> {
                    if (model.placeId != null) {
                        return getPlaceUseCase.execute(model.placeId)
                                              .map(place -> {
                                                  model.placeName = place.getName().toString();
                                                  model.placeAddress = place.getAddress().toString();
                                                  return model;
                                              })
                                              .toObservable();
                    } else {
                        return Observable.just(model);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    getView().onModelUpdated(model);
                }, e -> {
                    getView().onSnackbar(R.string.snackbar_failed);
                    getLog().e(String.format("Failed: areaId = %s", areaId), e);
                });
        manageDisposable(disposable);
    }

    public void onEdit() {
        getView().onShowUserAreaEditView(areaId);
    }

    public void onClickButtonUserAreaDescriptionsInArea() {
        getView().onShowUserAreaDescriptionsInAreaView(areaId);
    }
}
