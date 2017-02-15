package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.PlaceModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserAreaEditModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaEditModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaEditView;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserArea;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserAreaUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaEditPresenter extends BasePresenter<UserAreaEditView> {

    private static final String ARG_AREA_ID = "areaId";

    private static final String STATE_MODEL = "model";

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    SaveUserAreaUseCase saveUserAreaUseCase;

    @Inject
    CurrentUserResolver currentUserResolver;

    private String areaId;

    private UserAreaEditModel model;

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

        if (model == null) {
            if (areaId == null) {
                areaId = UUID.randomUUID().toString();
                model = new UserAreaEditModel();
                model.userId = currentUserResolver.getUserId();
                model.areaId = areaId;
                getView().onUpdateButtonRemovePlaceEnabled(canRemovePlace());
                getView().onUpdateButtonSaveEnabled(canSave());
            } else {
                getView().onUpdateViewsEnabled(false);

                Disposable disposable = findUserAreaUseCase
                        .execute(areaId)
                        .map(UserAreaEditModelMapper::map)
                        .flatMap(model -> {
                            if (model.placeId == null) {
                                return Maybe.just(model);
                            } else {
                                return getPlaceUseCase
                                        .execute(model.placeId)
                                        .map(place -> {
                                            model.place = PlaceModelMapper.map(place);
                                            return model;
                                        })
                                        .toMaybe();
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(model -> {
                            this.model = model;
                            getView().onUpdateTitle(model.name);
                            getView().onUpdateName(model.name);
                            getView().onUpdatePlaceName(model.place == null ? null : model.place.name);
                            getView().onUpdatePlaceAddress(model.place == null ? null : model.place.address);
                            getView().onUpdateLevel(model.level);
                            getView().onUpdateViewsEnabled(true);
                            getView().onUpdateButtonRemovePlaceEnabled(canRemovePlace());
                            getView().onUpdateButtonSaveEnabled(canSave());
                        }, e -> {
                            getLog().e("Failed.", e);
                            getView().onSnackbar(R.string.snackbar_failed);
                        }, () -> {
                            getLog().e("Entity not found.");
                            getView().onSnackbar(R.string.snackbar_failed);
                        });
                manageDisposable(disposable);
            }
        } else {
            getView().onUpdateTitle(model.name);
            getView().onUpdateName(model.name);
            getView().onUpdatePlaceName(model.place == null ? null : model.place.name);
            getView().onUpdatePlaceAddress(model.place == null ? null : model.place.address);
            getView().onUpdateLevel(model.level);
            getView().onUpdateViewsEnabled(true);
            getView().onUpdateButtonRemovePlaceEnabled(canRemovePlace());
            getView().onUpdateButtonSaveEnabled(canSave());
        }
    }

    public void onEditTextNameAfterTextChanged(String name) {
        model.name = name;
        getView().onHideNameError();

        // Don't save the empty name.
        if (name == null || name.length() == 0) {
            getView().onShowNameError(R.string.input_error_name_required);
        }

        getView().onUpdateButtonSaveEnabled(canSave());
    }

    public void onClickImageButtonPickPlace() {
        getView().onShowPlacePicker();
    }

    public void onPlacePicked(@NonNull Place place) {
        // onPlacePicked will be invoked after Fragment#onStart() because of the result of startActivityForResult.
        model.placeId = place.getId();
        model.place = PlaceModelMapper.map(place);

        getView().onUpdatePlaceName(model.place.name);
        getView().onUpdatePlaceAddress(model.place.address);
        getView().onUpdateButtonRemovePlaceEnabled(canRemovePlace());
    }

    public void onClickImageButtonRemovePlace() {
        model.placeId = null;
        model.place = null;

        getView().onUpdatePlaceName(null);
        getView().onUpdatePlaceAddress(null);
        getView().onUpdateButtonRemovePlaceEnabled(canRemovePlace());
    }

    public void onItemSelectedSpinnerLevel(int level) {
        if (model == null) return;

        model.level = level;
    }

    public void onClickButtonSave() {
        getView().onUpdateViewsEnabled(false);

        UserArea userArea = UserAreaEditModelMapper.map(model);

        Disposable disposable = saveUserAreaUseCase
                .execute(userArea)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getView().onUpdateTitle(model.name);
                    getView().onUpdateViewsEnabled(true);
                    getView().onUpdateButtonRemovePlaceEnabled(canRemovePlace());
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    private boolean canRemovePlace() {
        return model.placeId != null;
    }

    private boolean canSave() {
        return model.name != null && model.name.length() != 0;
    }
}
