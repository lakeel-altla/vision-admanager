package com.lakeel.altla.vision.admanager.presentation.presenter;

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

                        view.showModel(this.model);
                        modelLoaded = true;
                    }, e -> {
                        LOG.e(String.format("Failed to find the user area description: areaDescriptionId = %s",
                                            model.areaDescriptionId), e);
                    });
            compositeSubscription.add(subscription);
        }
    }

    public void onPause() {
        compositeSubscription.clear();
    }

    public void onAfterTextChangedName(String name) {
        model.name = name;

        LOG.d("onAfterTextChangedName: name = %s", name);

        // Don't save the empty name.
        if (name == null || name.length() == 0) {
            view.showNameError(R.string.input_error_name_required);
            return;
        }

        view.hideNameError();

        UserAreaDescription userAreaDescription = new UserAreaDescription(
                model.areaDescriptionId, model.name, model.creationTime);

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
