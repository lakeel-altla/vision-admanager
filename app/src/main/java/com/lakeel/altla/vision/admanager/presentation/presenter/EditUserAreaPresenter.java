package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.EditUserAreaModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaModel;
import com.lakeel.altla.vision.admanager.presentation.view.EditUserAreaView;
import com.lakeel.altla.vision.domain.model.UserArea;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserAreaUseCase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class EditUserAreaPresenter {

    private static final Log LOG = LogFactory.getLog(EditUserAreaPresenter.class);

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    SaveUserAreaUseCase saveUserAreaUseCase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String areaId;

    private EditUserAreaView view;

    private EditUserAreaModel model;

    private boolean processing;

    @Inject
    public EditUserAreaPresenter() {
    }

    public void onCreate(@Nullable String areaId) {
        this.areaId = areaId;
    }

    public void onCreateView(@NonNull EditUserAreaView view) {
        this.view = view;
    }

    public void onStart() {
        if (areaId != null) {
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
                    .subscribe(model -> {
                        this.model = model;
                        view.showModel(model);
                    }, e -> {
                        view.showSnackbar(R.string.snackbar_failed);
                        LOG.e(String.format("Failed to find the user area: areaId = %s", areaId), e);
                    });
            compositeDisposable.add(disposable);
        } else {
            areaId = UUID.randomUUID().toString();
        }
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public void onEditTextNameAfterTextChanged(String name) {
        if (processing) return;
        processing = true;

        model.name = name;

        save();
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

        save();
    }

    public void onClickImageButtonRemovePlace() {
        if (processing) return;
        processing = true;

        model.placeId = null;
        model.placeName = null;
        model.placeAddress = null;

        view.showModel(model);

        save();
    }

    public void onItemSelectedSpinnerLevel(int level) {
        if (processing) return;
        processing = true;

        model.level = level;

        save();
    }

    private void save() {
        UserArea userArea = new UserArea();
        userArea.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userArea.areaId = model.areaId;
        userArea.name = model.name;
        userArea.placeId = model.placeId;
        userArea.level = model.level;

        Disposable disposable = saveUserAreaUseCase
                .execute(userArea)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> processing = false)
                .subscribe(() -> {
                }, e -> {
                    LOG.e(String.format("Failed to save the user area: areaId = %s", areaId), e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }
}
