package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaDescriptionModel;
import com.lakeel.altla.vision.admanager.presentation.view.EditUserAreaDescriptionView;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserAreaDescriptionUseCase;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class EditUserAreaDescriptionPresenter {

    private static final Log LOG = LogFactory.getLog(EditUserAreaDescriptionModel.class);

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    @Inject
    SaveUserAreaDescriptionUseCase saveUserAreaDescriptionUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private EditUserAreaDescriptionView view;

    private String areaDescriptionId;

    private EditUserAreaDescriptionModel model;

    private boolean processing;

    @Inject
    public EditUserAreaDescriptionPresenter() {
    }

    public void onCreate(@NonNull String areaDescriptionId) {
        this.areaDescriptionId = areaDescriptionId;
    }

    public void onCreateView(@NonNull EditUserAreaDescriptionView view) {
        this.view = view;
    }

    public void onStart() {
        processing = true;

        Disposable disposable = findUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .map(userAreaDescription -> {
                    EditUserAreaDescriptionModel model = new EditUserAreaDescriptionModel();
                    model.areaDescriptionId = areaDescriptionId;
                    model.name = userAreaDescription.name;
                    model.creationTime = userAreaDescription.creationTime;
                    model.placeId = userAreaDescription.placeId;
                    model.level = userAreaDescription.level;
                    return model;
                })
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
                    view.showModel(model);
                }, e -> {
                    LOG.e(String.format("Failed to find the user area description: areaDescriptionId = %s",
                                        areaDescriptionId), e);
                });
        compositeDisposable.add(disposable);
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public void onEditTextNameAfterTextChanged(String name) {
        if (processing) return;
        processing = true;

        model.name = name;
        view.hideNameError();

        // Don't save the empty name.
        if (name == null || name.length() == 0) {
            processing = false;
            view.showNameError(R.string.input_error_name_required);
        } else {
            saveUserAreaDescription();
        }
    }

    public void onClickImageButtonPickPlace() {
        view.showPlacePicker();
    }

    public void onPlacePicked(@NonNull Place place) {
        // NOTE:
        // onPlacePicked will be invoked before onResume().

        if (processing) return;
        processing = true;

        model.placeId = place.getId();
        model.placeName = place.getName().toString();
        model.placeAddress = place.getAddress().toString();

        view.showModel(model);

        saveUserAreaDescription();
    }

    public void onClickImageButtonRemovePlace() {
        if (processing) return;
        processing = true;

        model.placeId = null;
        model.placeName = null;
        model.placeAddress = null;

        view.showModel(model);

        saveUserAreaDescription();
    }

    public void onItemSelectedSpinnerLevel(int level) {
        if (processing) return;
        processing = true;

        model.level = level;

        saveUserAreaDescription();
    }

    private void saveUserAreaDescription() {
        UserAreaDescription userAreaDescription = new UserAreaDescription();
        userAreaDescription.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userAreaDescription.areaDescriptionId = areaDescriptionId;
        userAreaDescription.name = model.name;
        userAreaDescription.creationTime = model.creationTime;
        userAreaDescription.placeId = model.placeId;
        userAreaDescription.level = model.level;

        Disposable disposable = saveUserAreaDescriptionUseCase
                .execute(userAreaDescription)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> processing = false)
                .subscribe(() -> {
                }, e -> {
                    LOG.e(String.format("Failed to save the user area description: areaDescriptionId = %s",
                                        areaDescriptionId), e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }
}
