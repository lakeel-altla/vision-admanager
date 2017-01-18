package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaDescriptionModel;
import com.lakeel.altla.vision.admanager.presentation.view.EditUserAreaDescriptionView;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserAreaDescriptionUseCase;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class EditUserAreaDescriptionPresenter {

    private static final Log LOG = LogFactory.getLog(EditUserAreaDescriptionModel.class);

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    @Inject
    SaveUserAreaDescriptionUseCase saveUserAreaDescriptionUseCase;

    @Inject
    GoogleApiClient googleApiClient;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private EditUserAreaDescriptionView view;

    private boolean modelLoaded;

    private EditUserAreaDescriptionModel model;

    @Inject
    public EditUserAreaDescriptionPresenter() {
    }

    public void onCreate(@NonNull String areaDescriptionId) {
        model = new EditUserAreaDescriptionModel(areaDescriptionId);
    }

    public void onCreateView(@NonNull EditUserAreaDescriptionView view) {
        this.view = view;
    }

    public void onResume() {
        if (!modelLoaded) {
            Subscription subscription = findUserAreaDescriptionUseCase
                    .execute(model.areaDescriptionId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userAreaDescription -> {
                        model.name = userAreaDescription.name;
                        model.creationTime = userAreaDescription.creationTime;
                        model.placeId = userAreaDescription.placeId;
                        model.level = userAreaDescription.level;

                        view.showModel(this.model);
                        modelLoaded = true;

                        // TODO: implement as use-case class.
                        // TODO: implement the progress ring to indicate loading.
                        if (userAreaDescription.placeId != null && userAreaDescription.placeId.length() != 0) {
                            Places.GeoDataApi
                                    .getPlaceById(googleApiClient, userAreaDescription.placeId)
                                    .setResultCallback(places -> {
                                        if (places.getStatus().isSuccess()) {
                                            Place place = places.get(0);

                                            model.placeName = place.getName().toString();
                                            model.placeAddress = place.getAddress().toString();

                                            view.showModel(model);
                                        } else if (places.getStatus().isCanceled()) {
                                            LOG.e("Getting the place was canceled: placeId = %s",
                                                  userAreaDescription.placeId);
                                        } else if (places.getStatus().isInterrupted()) {
                                            LOG.e("Getting the place was interrupted: placeId = %s",
                                                  userAreaDescription.placeId);
                                        }
                                    });
                        }
                    }, e -> {
                        LOG.e(String.format("Failed to find the user area description: areaDescriptionId = %s",
                                            model.areaDescriptionId), e);
                    });
            compositeSubscription.add(subscription);
        }
    }

    public void onPause() {
        compositeSubscription.clear();
        modelLoaded = false;
    }

    public void onAfterTextChangedName(String name) {
        if (modelLoaded) {
            model.name = name;

            // Don't save the empty name.
            if (name == null || name.length() == 0) {
                view.showNameError(R.string.input_error_name_required);
                return;
            }

            view.hideNameError();

            saveUserAreaDescription();
        }
    }

    public void onClickImageButtonPickPlace() {
        if (modelLoaded) {
            view.showPlacePicker();
        }
    }

    public void onPlacePicked(@NonNull Place place) {
        if (modelLoaded) {
            model.placeId = place.getId();
            model.placeName = place.getName().toString();
            model.placeAddress = place.getAddress().toString();

            view.showModel(model);

            saveUserAreaDescription();
        }
    }

    public void onClickImageButtonRemovePlace() {
        // TODO: and if place loaded...
        if (modelLoaded) {
            model.placeId = null;
            model.placeName = null;
            model.placeAddress = null;

            view.showModel(model);

            saveUserAreaDescription();
        }
    }

    public void onItemSelectedSpinnerLevel(int level) {
        if (modelLoaded) {
            model.level = level;

            saveUserAreaDescription();
        }
    }

    private void saveUserAreaDescription() {
        UserAreaDescription userAreaDescription = new UserAreaDescription();
        userAreaDescription.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userAreaDescription.areaDescriptionId = model.areaDescriptionId;
        userAreaDescription.name = model.name;
        userAreaDescription.creationTime = model.creationTime;
        userAreaDescription.placeId = model.placeId;
        userAreaDescription.level = model.level;

        Subscription subscription = saveUserAreaDescriptionUseCase
                .execute(userAreaDescription)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, e -> {
                    LOG.e(String.format("Failed to save the user area description: areaDescriptionId = %s",
                                        model.areaDescriptionId), e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeSubscription.add(subscription);
    }
}
