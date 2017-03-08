package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneEditView;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.Scene;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserSceneUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserSceneUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class UserSceneEditPresenter extends BasePresenter<UserSceneEditView> {

    private static final String ARG_SCENE_ID = "sceneId";

    private static final String STATE_MODEL = "model";

    @Inject
    FindUserSceneUseCase findUserSceneUseCase;

    @Inject
    SaveUserSceneUseCase saveUserSceneUseCase;

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    CurrentUserResolver currentUserResolver;

    private String sceneId;

    private Model model;

    @Inject
    public UserSceneEditPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@Nullable String sceneId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SCENE_ID, sceneId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments != null) {
            sceneId = arguments.getString(ARG_SCENE_ID, null);
        }

        if (savedInstanceState == null) {
            model = null;
        } else {
            model = Parcels.unwrap(savedInstanceState.getParcelable(STATE_MODEL));
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
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);
        getView().onUpdateTitle(null);

        if (model == null) {
            if (sceneId == null) {
                model = new Model();
                model.scene.setUserId(currentUserResolver.getUserId());
                getView().onShowNameError(R.string.input_error_name_required);
                getView().onUpdateViewsEnabled(true);
                getView().onUpdateActionSave(model.canSave());
            } else {
                getView().onUpdateViewsEnabled(false);

                Disposable disposable = findUserSceneUseCase
                        .execute(sceneId)
                        .map(Model::new)
                        .flatMap(model -> {
                            if (model.scene.getAreaId() != null) {
                                return findUserAreaUseCase
                                        .execute(model.scene.getAreaId())
                                        .map(userArea -> {
                                            model.areaName = userArea.getName();
                                            return model;
                                        });
                            } else {
                                return Maybe.just(model);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(model -> {
                            this.model = model;
                            getView().onUpdateTitle(model.scene.getName());
                            getView().onUpdateName(model.scene.getName());
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
            }
        } else {
            if (model.areaNameDirty) {
                getView().onUpdateViewsEnabled(false);

                if (model.scene.getAreaId() == null) {
                    model.areaName = null;
                    model.areaNameDirty = false;
                    getView().onUpdateAreaName(model.areaName);
                    getView().onUpdateViewsEnabled(true);
                    getView().onUpdateActionSave(model.canSave());
                } else {
                    Disposable disposable = findUserAreaUseCase
                            .execute(model.scene.getAreaId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(userArea -> {
                                model.areaName = userArea.getName();
                                model.areaNameDirty = false;
                                getView().onUpdateAreaName(model.areaName);
                                getView().onUpdateViewsEnabled(true);
                                getView().onUpdateActionSave(model.canSave());
                            }, e -> {
                                getLog().e("Failed.", e);
                                getView().onSnackbar(R.string.snackbar_failed);
                            });
                    manageDisposable(disposable);
                }
            } else {
                getView().onUpdateTitle(model.scene.getName());
                getView().onUpdateName(model.scene.getName());
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
        model.scene.setName(value);
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
        model.scene.setAreaId(areaId);
        model.areaNameDirty = true;
    }

    public void onActionSave() {
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);

        Disposable disposable = saveUserSceneUseCase
                .execute(model.scene)
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
    public static final class Model {

        Scene scene;

        String areaName;

        boolean areaNameDirty;

        Model() {
            this(new Scene());
        }

        Model(@NonNull Scene scene) {
            this.scene = scene;
        }

        boolean canSave() {
            return scene.getName() != null && scene.getName().length() != 0;
        }
    }
}
