package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaView;
import com.lakeel.altla.vision.domain.model.UserArea;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveUserAreaUseCase;
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
    ObserveUserAreaUseCase observeUserAreaUseCase;

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
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        getView().onUpdateTitle(null);

        Disposable disposable = observeUserAreaUseCase
                .execute(areaId)
                .map(this::map)
                .flatMap(model -> {
                    if (model.placeId == null) {
                        return Observable.just(model);
                    } else {
                        return getPlaceUseCase
                                .execute(model.placeId)
                                .map(place -> {
                                    model.placeName = place.getName().toString();
                                    model.placeAddress = place.getAddress().toString();
                                    return model;
                                })
                                .toObservable();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    getView().onUpdateTitle(model.name);
                    getView().onUpdateAreaId(model.areaId);
                    getView().onUpdateName(model.name);
                    getView().onUpdatePlaceName(model.placeName);
                    getView().onUpdatePlaceAddress(model.placeAddress);
                    getView().onUpdateLevel(model.level);
                    getView().onUpdateCreatedAt(model.createdAt);
                    getView().onUpdateUpdatedAt(model.updatedAt);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onEdit() {
        getView().onShowUserAreaEditView(areaId);
    }

    public void onClickButtonUserAreaDescriptionsInArea() {
        getView().onShowUserAreaDescriptionListInAreaView(areaId);
    }

    public void onClickButtonUserScenesInArea() {
        getView().onShowUserSceneListInAreaView(areaId);
    }

    @NonNull
    private Model map(@NonNull UserArea userArea) {
        Model model = new Model();
        model.areaId = userArea.areaId;
        model.name = userArea.name;
        model.placeId = userArea.placeId;
        model.level = userArea.level;
        model.createdAt = userArea.createdAt;
        model.updatedAt = userArea.updatedAt;
        return model;
    }

    private final class Model {

        String areaId;

        String name;

        String placeId;

        String placeName;

        String placeAddress;

        int level;

        long createdAt;

        long updatedAt;
    }
}
