package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.EditUserAreaModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaModel;
import com.lakeel.altla.vision.admanager.presentation.view.EditUserAreaView;
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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class EditUserAreaPresenter extends BasePresenter<EditUserAreaView> {

    private static final String ARG_AREA_ID = "areaId";

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    SaveUserAreaUseCase saveUserAreaUseCase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String areaId;

    private EditUserAreaModel model;

    private boolean creatingNew;

    private boolean processing;

    @Inject
    public EditUserAreaPresenter() {
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
    public void onStart() {
        super.onStart();

        if (areaId != null) {
            processing = true;

            Disposable disposable = findUserAreaUseCase
                    .execute(areaId)
                    .map(EditUserAreaModelMapper::map)
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
                        getView().showModel(model);
                    }, e -> {
                        getView().showSnackbar(R.string.snackbar_failed);
                        getLog().e(String.format("Failed to find the user area: areaId = %s", areaId), e);
                    });
            compositeDisposable.add(disposable);
        } else {
            creatingNew = true;
            model = new EditUserAreaModel();

            getView().showModel(model);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        compositeDisposable.clear();
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
        getView().showPlacePicker();
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

        getView().showModel(model);

        save();
    }

    public void onClickImageButtonRemovePlace() {
        if (processing) return;

        processing = true;

        model.placeId = null;
        model.placeName = null;
        model.placeAddress = null;

        getView().showModel(model);

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
            getView().showAreaId(areaId);
            getView().showCreatedAt(model.createdAt);
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
                    getView().showSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }
}
