package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaEditView;
import com.lakeel.altla.vision.api.CurrentUser;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class UserAreaEditPresenter extends BasePresenter<UserAreaEditView> {

    private static final String ARG_AREA_ID = "areaId";

    private static final String STATE_MODEL = "model";

    @Inject
    VisionService visionService;

    @Inject
    GoogleApiClient googleApiClient;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String areaId;

    private Model model;

    @Inject
    public UserAreaEditPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@Nullable String areaId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_ID, areaId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) {
            areaId = null;
        } else {
            areaId = arguments.getString(ARG_AREA_ID, null);
        }

        if (savedInstanceState == null) {
            model = null;
        } else {
            model = Parcels.unwrap(savedInstanceState.getParcelable(STATE_MODEL));
        }
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_MODEL, Parcels.wrap(model));
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        getView().onUpdateHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        getView().onUpdateTitle(null);
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);

        if (model == null) {
            if (areaId == null) {
                model = new Model();
                model.area.setUserId(CurrentUser.getInstance().getUserId());
                getView().onUpdateLevel(model.area.getLevel());
                getView().onUpdateViewsEnabled(true);
                getView().onUpdateActionSave(model.canSave());
                getView().onUpdateButtonRemovePlaceEnabled(model.canRemovePlace());
            } else {
                Disposable disposable = Maybe.<Model>create(e -> {
                    visionService.getUserAreaApi().findAreaById(areaId, area -> {
                        Model model = new Model(area);

                        if (area.getPlaceId() == null) {
                            e.onSuccess(model);
                        } else {
                            visionService.getGooglePlaceApi()
                                         .getPlaceById(googleApiClient, area.getPlaceId(), place -> {
                                             model.placeName = place.getName().toString();
                                             model.placeAddress = place.getAddress().toString();
                                             e.onSuccess(model);
                                         }, e::onError);
                        }
                    }, e::onError);
                }).subscribe(model -> {
                    this.model = model;
                    getView().onUpdateTitle(model.area.getName());
                    getView().onUpdateName(model.area.getName());
                    getView().onUpdatePlaceName(model.placeName);
                    getView().onUpdatePlaceAddress(model.placeAddress);
                    getView().onUpdateLevel(model.area.getLevel());
                    getView().onUpdateViewsEnabled(true);
                    getView().onUpdateActionSave(model.canSave());
                    getView().onUpdateButtonRemovePlaceEnabled(model.canRemovePlace());
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                }, () -> {
                    getLog().e("Entity not found.");
                    getView().onSnackbar(R.string.snackbar_failed);
                });
                compositeDisposable.add(disposable);
            }
        } else {
            getView().onUpdateTitle(model.area.getName());
            getView().onUpdateName(model.area.getName());
            getView().onUpdatePlaceName(model.placeName);
            getView().onUpdatePlaceAddress(model.placeAddress);
            getView().onUpdateLevel(model.area.getLevel());
            getView().onUpdateViewsEnabled(true);
            getView().onUpdateActionSave(model.canSave());
            getView().onUpdateButtonRemovePlaceEnabled(model.canRemovePlace());
        }
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        compositeDisposable.clear();
        getView().onUpdateHomeAsUpIndicator(null);
    }

    public void onEditTextNameAfterTextChanged(String value) {
        model.area.setName(value);
        getView().onHideNameError();

        // Don't save the empty value.
        if (value == null || value.length() == 0) {
            getView().onShowNameError(R.string.input_error_name_required);
        }

        getView().onUpdateActionSave(model.canSave());
    }

    public void onClickImageButtonPickPlace() {
        getView().onShowPlacePicker();
    }

    public void onPlacePicked(@NonNull Place place) {
        // onPlacePicked will be invoked after Fragment#onStart() because of the result of startActivityForResult.
        model.area.setPlaceId(place.getId());
        model.placeName = place.getName().toString();
        model.placeAddress = place.getAddress().toString();

        getView().onUpdatePlaceName(model.placeName);
        getView().onUpdatePlaceAddress(model.placeAddress);
        getView().onUpdateButtonRemovePlaceEnabled(model.canRemovePlace());
    }

    public void onClickImageButtonRemovePlace() {
        model.area.setPlaceId(null);
        model.placeName = null;
        model.placeAddress = null;

        getView().onUpdatePlaceName(null);
        getView().onUpdatePlaceAddress(null);
        getView().onUpdateButtonRemovePlaceEnabled(model.canRemovePlace());
    }

    public void onItemSelectedSpinnerLevel(int level) {
        if (model == null) return;

        model.area.setLevel(level);
    }

    public void onActionSave() {
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);

        Disposable disposable = Completable.create(e -> {
            visionService.getUserAreaApi().saveArea(model.area);
            e.onComplete();
        }).subscribe(() -> {
            getView().onSnackbar(R.string.snackbar_done);
            getView().onBackView();
        }, e -> {
            getLog().e("Failed.", e);
            getView().onSnackbar(R.string.snackbar_failed);
        });
        compositeDisposable.add(disposable);
    }

    @Parcel
    public static final class Model {

        Area area;

        String placeName;

        String placeAddress;

        Model() {
            this(new Area());
        }

        Model(@NonNull Area area) {
            this.area = area;
        }

        boolean canRemovePlace() {
            return area.getPlaceId() != null;
        }

        boolean canSave() {
            return area.getName() != null && area.getName().length() != 0;
        }
    }
}
