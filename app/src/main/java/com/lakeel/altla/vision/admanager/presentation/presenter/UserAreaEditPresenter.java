package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserAreaModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaEditView;
import com.lakeel.altla.vision.domain.model.UserArea;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserAreaUseCase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaEditPresenter extends BasePresenter<UserAreaEditView> {

    private static final String ARG_AREA_ID = "areaId";

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    SaveUserAreaUseCase saveUserAreaUseCase;

    private String areaId;

    private UserAreaModel model;

    private boolean creatingNew;

    private boolean processing;

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

        if (arguments != null) {
            areaId = arguments.getString(ARG_AREA_ID, null);
        }
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        if (areaId != null) {
            processing = true;

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
                    .doOnTerminate(() -> processing = false)
                    .subscribe(model -> {
                        this.model = model;
                        getView().onModelUpdated(model);
                    }, e -> {
                        getView().onSnackbar(R.string.snackbar_failed);
                        getLog().e(String.format("Failed to get the user area: areaId = %s", areaId), e);
                    });
            manageDisposable(disposable);
        } else {
            creatingNew = true;
            model = new UserAreaModel();

            getView().onModelUpdated(model);
        }
    }

    public void onEditTextNameAfterTextChanged(String name) {
        if (processing) return;
        if (name != null && name.length() == 0) {
            name = null;
        }
        if (model.name == null && name == null) return;
        if (name != null && name.equals(model.name)) return;
        if (model.name != null && model.name.equals(name)) return;

        processing = true;

        model.name = name;

        save();
    }

    public void onClickImageButtonPickPlace() {
        getView().onShowPlacePicker();
    }

    public void onPlacePicked(@NonNull Place place) {
        // NOTE:
        // onPlacePicked will be invoked before onResume().

        if (processing) return;
        if (place.getId().equals(model.placeId)) return;

        processing = true;

        model.placeId = place.getId();
        model.placeName = place.getName().toString();
        model.placeAddress = place.getAddress().toString();

        getView().onModelUpdated(model);

        save();
    }

    public void onClickImageButtonRemovePlace() {
        if (processing) return;

        processing = true;

        model.placeId = null;
        model.placeName = null;
        model.placeAddress = null;

        getView().onModelUpdated(model);

        save();
    }

    public void onItemSelectedSpinnerLevel(int level) {
        if (processing) return;
        if (model.level == level) return;

        processing = true;

        model.level = level;

        save();
    }

    private void save() {
        if (creatingNew) {
            areaId = UUID.randomUUID().toString();
            model.areaId = areaId;
            model.createdAt = System.currentTimeMillis();
            getView().onAreaIdUpdated(areaId);
            getView().onCreatedAtUpdated(model.createdAt);
            creatingNew = false;
        }

        UserArea userArea = new UserArea();
        userArea.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userArea.areaId = model.areaId;
        userArea.name = model.name;
        userArea.createdAt = model.createdAt;

        userArea.placeId = model.placeId;
        userArea.level = model.level;

        Disposable disposable = saveUserAreaUseCase
                .execute(userArea)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> processing = false)
                .subscribe(() -> {
                }, e -> {
                    getLog().e(String.format("Failed: areaId = %s", areaId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }
}
