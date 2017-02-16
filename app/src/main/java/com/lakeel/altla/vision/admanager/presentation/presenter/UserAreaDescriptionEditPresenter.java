package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionEditView;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionEditPresenter extends BasePresenter<UserAreaDescriptionEditView> {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    private static final String STATE_MODEL = "model";

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    @Inject
    SaveUserAreaDescriptionUseCase saveUserAreaDescriptionUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    Resources resources;

    private String areaDescriptionId;

    private Model model;

    private boolean areaNameDirty;

    @Inject
    public UserAreaDescriptionEditPresenter() {
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

        if (savedInstanceState != null) {
            model = Parcels.unwrap(savedInstanceState.getParcelable(STATE_MODEL));
        } else {
            model = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_MODEL, Parcels.wrap(model));
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        // Clear the previous title.
        getView().onUpdateTitle(null);

        if (model == null) {
            getView().onUpdateViewsEnabled(false);

            Disposable disposable = findUserAreaDescriptionUseCase
                    .execute(areaDescriptionId)
                    .map(UserAreaDescriptionEditPresenter::map)
                    .flatMap(model -> {
                        if (model.areaId == null) {
                            return Maybe.just(model);
                        } else {
                            return findUserAreaUseCase
                                    .execute(model.areaId)
                                    .map(userArea -> {
                                        model.areaName = userArea.name;
                                        return model;
                                    });
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(model -> {
                        this.model = model;

                        getView().onUpdateTitle(model.name);
                        getView().onUpdateName(model.name);
                        getView().onUpdateAreaName(model.areaName);
                        getView().onUpdateViewsEnabled(true);
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    }, () -> {
                        getLog().e("Entity not found.");
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            manageDisposable(disposable);
        } else {
            getView().onUpdateTitle(model.name);
            getView().onUpdateName(model.name);

            if (areaNameDirty) {
                areaNameDirty = false;

                getView().onUpdateViewsEnabled(false);

                Disposable disposable = findUserAreaUseCase
                        .execute(model.areaId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userArea -> {
                            model.areaName = userArea.name;
                            getView().onUpdateAreaName(model.areaName);
                            getView().onUpdateViewsEnabled(true);
                        });
                manageDisposable(disposable);
            } else {
                getView().onUpdateAreaName(model.areaName);
            }
        }
    }

    public void onEditTextNameAfterTextChanged(String name) {
        model.name = name;
        getView().onHideNameError();

        // Don't save the empty name.
        if (name == null || name.length() == 0) {
            getView().onShowNameError(R.string.input_error_name_required);
        }
    }

    public void onClickImageButtonSelectArea() {
        getView().onShowUserAreaSelectView();
    }

    public void onUserAreaSelected(@NonNull String areaId) {
        // This method will be called before Fragment#onStart().
        model.areaId = areaId;
        areaNameDirty = true;
    }

    public void onClickButtonSave() {
        getView().onUpdateViewsEnabled(false);

        UserAreaDescription userAreaDescription = map(model);

        Disposable disposable = saveUserAreaDescriptionUseCase
                .execute(userAreaDescription)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getView().onUpdateTitle(model.name);
                    getView().onUpdateViewsEnabled(true);
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }


    @NonNull
    public static Model map(@NonNull UserAreaDescription userAreaDescription) {
        Model model = new Model();
        model.userId = userAreaDescription.userId;
        model.areaDescriptionId = userAreaDescription.areaDescriptionId;
        model.name = userAreaDescription.name;
        model.fileUploaded = userAreaDescription.fileUploaded;
        model.areaId = userAreaDescription.areaId;
        model.createdAt = userAreaDescription.createdAt;
        model.updatedAt = userAreaDescription.updatedAt;
        return model;
    }

    @NonNull
    public static UserAreaDescription map(@NonNull Model model) {
        UserAreaDescription userAreaDescription = new UserAreaDescription(model.userId, model.areaDescriptionId);
        userAreaDescription.name = model.name;
        userAreaDescription.fileUploaded = model.fileUploaded;
        userAreaDescription.areaId = model.areaId;
        userAreaDescription.createdAt = model.createdAt;
        userAreaDescription.updatedAt = model.updatedAt;
        return userAreaDescription;
    }

    @Parcel
    public static class Model {

        String userId;

        String areaDescriptionId;

        String name;

        boolean fileUploaded;

        String areaId;

        String areaName;

        long createdAt;

        long updatedAt;
    }
}
