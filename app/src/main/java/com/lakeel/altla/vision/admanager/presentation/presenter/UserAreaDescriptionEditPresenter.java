package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionEditView;
import com.lakeel.altla.vision.domain.model.AreaDescription;
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

        getView().onUpdateHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        // Clear the previous title.
        getView().onUpdateTitle(null);
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);

        if (model == null) {
            Disposable disposable = findUserAreaDescriptionUseCase
                    .execute(areaDescriptionId)
                    .map(Model::new)
                    .flatMap(model -> {
                        if (model.areaDescription.getAreaId() == null) {
                            return Maybe.just(model);
                        } else {
                            return findUserAreaUseCase
                                    .execute(model.areaDescription.getAreaId())
                                    .map(userArea -> {
                                        model.areaName = userArea.getName();
                                        return model;
                                    });
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(model -> {
                        this.model = model;

                        getView().onUpdateTitle(model.areaDescription.getName());
                        getView().onUpdateName(model.areaDescription.getName());
                        getView().onUpdateAreaName(model.areaName);
                        getView().onUpdateViewsEnabled(true);
                        getView().onUpdateActionSave(model.canSave());
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    }, () -> {
                        getLog().e("Entity not found.");
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            manageDisposable(disposable);
        } else {
            getView().onUpdateTitle(model.areaDescription.getName());
            getView().onUpdateName(model.areaDescription.getName());

            if (model.areaNameDirty) {
                if (model.areaDescription.getAreaId() == null) {
                    model.areaName = null;
                    model.areaNameDirty = false;
                    getView().onUpdateAreaName(model.areaName);
                    getView().onUpdateViewsEnabled(true);
                    getView().onUpdateActionSave(model.canSave());
                } else {
                    Disposable disposable = findUserAreaUseCase
                            .execute(model.areaDescription.getAreaId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(userArea -> {
                                model.areaName = userArea.getName();
                                model.areaNameDirty = false;
                                getView().onUpdateAreaName(model.areaName);
                                getView().onUpdateViewsEnabled(true);
                                getView().onUpdateActionSave(model.canSave());
                            });
                    manageDisposable(disposable);
                }
            } else {
                getView().onUpdateAreaName(model.areaName);
                getView().onUpdateViewsEnabled(true);
                getView().onUpdateActionSave(model.canSave());
            }
        }
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        getView().onUpdateHomeAsUpIndicator(null);
    }

    public void onEditTextNameAfterTextChanged(String value) {
        model.areaDescription.setName(value);
        getView().onHideNameError();

        // Don't save the empty value.
        if (value == null || value.length() == 0) {
            getView().onShowNameError(R.string.input_error_name_required);
        }

        getView().onUpdateActionSave(model.canSave());
    }

    public void onClickImageButtonSelectArea() {
        getView().onShowUserAreaSelectView();
    }

    public void onUserAreaSelected(@NonNull String areaId) {
        // This method will be called before Fragment#onStart().
        model.areaDescription.setAreaId(areaId);
        model.areaNameDirty = true;
    }

    public void onActionSave() {
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);

        Disposable disposable = saveUserAreaDescriptionUseCase
                .execute(model.areaDescription)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getView().onSnackbar(R.string.snackbar_done);
                    getView().onBackView();
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    @Parcel
    public static class Model {

        AreaDescription areaDescription;

        String areaName;

        boolean areaNameDirty;

        Model() {
            this(new AreaDescription());
        }

        Model(@NonNull AreaDescription areaDescription) {
            this.areaDescription = areaDescription;
        }

        boolean canSave() {
            return areaDescription.getName() != null && areaDescription.getName().length() != 0;
        }
    }
}
