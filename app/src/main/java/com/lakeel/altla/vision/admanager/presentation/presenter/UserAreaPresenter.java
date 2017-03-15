package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.common.api.GoogleApiClient;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaView;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.domain.helper.ObservableData;
import com.lakeel.altla.vision.domain.model.Area;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public final class UserAreaPresenter extends BasePresenter<UserAreaView> {

    private static final String ARG_AREA_ID = "areaId";

    @Inject
    VisionService visionService;

    @Inject
    GoogleApiClient googleApiClient;

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

        Disposable disposable = ObservableData
                .using(() -> visionService.getUserAreaApi().observeAreaById(areaId))
                .map(Model::new)
                .flatMap(model -> {
                    String placeId = model.area.getPlaceId();

                    if (placeId == null) {
                        return Observable.just(model);
                    } else {
                        return Observable.create(e -> {
                            visionService.getGooglePlaceApi().getPlaceById(googleApiClient, placeId, place -> {
                                model.placeName = place.getName().toString();
                                model.placeAddress = place.getAddress().toString();
                                e.onNext(model);
                                e.onComplete();
                            }, e::onError);
                        });
                    }
                })
                .subscribe(model -> {
                    getView().onUpdateTitle(model.area.getName());
                    getView().onUpdateAreaId(model.area.getId());
                    getView().onUpdateName(model.area.getName());
                    getView().onUpdatePlaceName(model.placeName);
                    getView().onUpdatePlaceAddress(model.placeAddress);
                    getView().onUpdateLevel(model.area.getLevel());
                    getView().onUpdateCreatedAt(model.area.getCreatedAtAsLong());
                    getView().onUpdateUpdatedAt(model.area.getUpdatedAtAsLong());
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
        getView().onShowUserAreaDescriptionByAreaListView(areaId);
    }

    private final class Model {

        final Area area;

        String placeName;

        String placeAddress;

        Model(@NonNull Area area) {
            this.area = area;
        }
    }
}
