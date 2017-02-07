package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserAreaDescriptionEditModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionEditModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionEditView;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

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

    private UserAreaDescriptionEditModel model;

    private boolean processing;

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
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        String format = resources.getString(R.string.title_format_user_area_description_edit);
        String title = String.format(format, areaDescriptionId);
        getView().onUpdateTitle(title);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        processing = true;

        Disposable disposable = findUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .map(UserAreaDescriptionEditModelMapper::map)
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
                    getView().onModelUpdated(model);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                });
        manageDisposable(disposable);
    }

    public void onEditTextNameAfterTextChanged(String name) {
        if (processing) return;
        processing = true;

        model.name = name;
        getView().onHideNameError();

        // Don't save the empty name.
        if (name == null || name.length() == 0) {
            processing = false;
            getView().onShowNameError(R.string.input_error_name_required);
        } else {
            saveUserAreaDescription();
        }
    }

    public void onClickImageButtonSelectArea() {
        getView().onShowUserAreaSelectView();
    }

    public void onUserAreaSelected(String areaId) {
        model.areaId = areaId;

        saveUserAreaDescription();

        Disposable disposable = findUserAreaUseCase
                .execute(areaId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userArea -> {
                    model.areaName = userArea.name;
                    getView().onAreaNameUpdated(model.areaName);
                }, e -> {
                    getLog().e(String.format("Failed: areaId = %s", areaId), e);
                });
        manageDisposable(disposable);
    }

    private void saveUserAreaDescription() {
        UserAreaDescription userAreaDescription = UserAreaDescriptionEditModelMapper.map(model);

        Disposable disposable = saveUserAreaDescriptionUseCase
                .execute(userAreaDescription)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> processing = false)
                .subscribe(() -> {
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }
}
