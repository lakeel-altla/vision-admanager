package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaView;
import com.lakeel.altla.vision.domain.model.UserArea;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
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
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        getView().onUpdateTitle(null);

        Disposable disposable = findUserAreaUseCase
                .execute(areaId)
                .map(userArea -> {
                    Model model = new Model();
                    model.userArea = userArea;
                    return model;
                })
                .flatMap(model -> {
                    if (model.userArea.placeId == null) {
                        return Maybe.just(model);
                    } else {
                        return getPlaceUseCase
                                .execute(model.userArea.placeId)
                                .map(place -> {
                                    model.place = place;
                                    return model;
                                })
                                .toMaybe();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    getView().onUpdateTitle(model.userArea.name);
                    getView().onUpdateAreaId(model.userArea.areaId);
                    getView().onUpdateName(model.userArea.name);
                    getView().onUpdatePlaceName(model.place == null ? null : model.place.getName().toString());
                    getView().onUpdatePlaceAddress(model.place == null ? null : model.place.getAddress().toString());
                    getView().onUpdateLevel(model.userArea.level);
                    getView().onUpdateCreatedAt(model.userArea.createdAt);
                    getView().onUpdateUpdatedAt(model.userArea.updatedAt);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                }, () -> {
                    getLog().e("Entity not found.");
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

    private final class Model {

        UserArea userArea;

        Place place;
    }
}
