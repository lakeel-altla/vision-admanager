package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.firebase.auth.FirebaseAuth;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaDescriptionModel;
import com.lakeel.altla.vision.admanager.presentation.view.EditUserAreaDescriptionView;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserAreaDescriptionUseCase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class EditUserAreaDescriptionPresenter extends BasePresenter<EditUserAreaDescriptionView> {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    @Inject
    SaveUserAreaDescriptionUseCase saveUserAreaDescriptionUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String areaDescriptionId;

    private EditUserAreaDescriptionModel model;

    private boolean processing;

    @Inject
    public EditUserAreaDescriptionPresenter() {
    }

    @NonNull
    public static Bundle createArguments(String areaDescriptionId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_DESCRIPTION_ID, areaDescriptionId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new IllegalStateException("Arguments must be not null.");

        String areaDescriptionId = arguments.getString(ARG_AREA_DESCRIPTION_ID);
        if (areaDescriptionId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_AREA_DESCRIPTION_ID));
        }

        this.areaDescriptionId = areaDescriptionId;
    }

    @Override
    public void onStart() {
        super.onStart();

        processing = true;

        Disposable disposable = findUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .map(userAreaDescription -> {
                    EditUserAreaDescriptionModel model = new EditUserAreaDescriptionModel();
                    model.areaDescriptionId = areaDescriptionId;
                    model.name = userAreaDescription.name;
                    model.creationTime = userAreaDescription.creationTime;
                    model.areaId = userAreaDescription.areaId;
                    return model;
                })
                .flatMap(model -> {
                    if (model.areaId != null) {
                        return findUserAreaUseCase
                                .execute(model.areaId)
                                .map(userArea -> {
                                    model.areaName = userArea.name;
                                    return model;
                                });
                    } else {
                        return Maybe.just(model);
                    }
                })
                .toSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(() -> processing = false)
                .subscribe(model -> {
                    this.model = model;
                    getView().showModel(model);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onStop() {
        super.onStop();

        compositeDisposable.clear();
    }

    public void onEditTextNameAfterTextChanged(String name) {
        if (processing) return;
        processing = true;

        model.name = name;
        getView().hideNameError();

        // Don't save the empty name.
        if (name == null || name.length() == 0) {
            processing = false;
            getView().showNameError(R.string.input_error_name_required);
        } else {
            saveUserAreaDescription();
        }
    }

    public void onClickImageButtonSelectArea() {
        getView().showSelectUserAreaView();
    }

    public void onUserAreaSelected(String areaId) {
        model.areaId = areaId;

        saveUserAreaDescription();

        Disposable disposable = findUserAreaUseCase
                .execute(areaId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userArea -> {
                    model.areaName = userArea.name;
                    getView().updateAreaName(model.areaName);
                }, e -> {
                    getLog().e(String.format("Failed: areaId = %s", areaId), e);
                });
        compositeDisposable.add(disposable);
    }

    private void saveUserAreaDescription() {
        UserAreaDescription userAreaDescription = new UserAreaDescription();
        userAreaDescription.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userAreaDescription.areaDescriptionId = areaDescriptionId;
        userAreaDescription.name = model.name;
        userAreaDescription.creationTime = model.creationTime;
        userAreaDescription.areaId = model.areaId;

        Disposable disposable = saveUserAreaDescriptionUseCase
                .execute(userAreaDescription)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> processing = false)
                .subscribe(() -> {
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                    getView().showSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }
}
