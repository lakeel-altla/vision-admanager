package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneEditView;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserScene;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserSceneUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserSceneUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

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

    private boolean areaFieldsDirty;

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

        if (model == null) {
            if (sceneId == null) {
                sceneId = UUID.randomUUID().toString();
                model = new Model();
                model.userId = currentUserResolver.getUserId();
                model.sceneId = sceneId;
                getView().onShowNameError(R.string.input_error_name_required);
                getView().onUpdateButtonSaveEnabled(false);
            } else {
                getView().onUpdateViewsEnabled(false);

                Disposable disposable = findUserSceneUseCase
                        .execute(sceneId)
                        .map(UserSceneEditPresenter::map)
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
            }
        } else {
            if (areaFieldsDirty) {
                areaFieldsDirty = false;
                getView().onUpdateViewsEnabled(false);

                Disposable disposable = findUserAreaUseCase
                        .execute(model.areaId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userArea -> {
                            model.areaName = userArea.name;
                            getView().onUpdateAreaName(model.areaName);
                            getView().onUpdateViewsEnabled(true);
                        }, e -> {
                            getLog().e("Failed.", e);
                            getView().onSnackbar(R.string.snackbar_failed);
                        });
                manageDisposable(disposable);
            } else {
                getView().onUpdateTitle(model.name);
                getView().onUpdateName(model.name);
                getView().onUpdateAreaName(model.areaName);
                getView().onUpdateViewsEnabled(true);
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

        getView().onUpdateButtonSaveEnabled(canSave());
    }

    public void onClickImageButtonSelectArea() {
        getView().onShowUserAreaSelectView();
    }

    public void onUserAreaSelected(@NonNull String areaId) {
        // This method will be called before Fragment#onStart().
        model.areaId = areaId;
        areaFieldsDirty = true;
    }

    public void onClickButtonSave() {
        getView().onUpdateViewsEnabled(false);

        UserScene userScene = map(model);

        Disposable disposable = saveUserSceneUseCase
                .execute(userScene)
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

    private boolean canSave() {
        return model.name != null && model.name.length() != 0;
    }

    @NonNull
    public static Model map(@NonNull UserScene userScene) {
        Model model = new Model();
        model.userId = userScene.userId;
        model.sceneId = userScene.sceneId;
        model.name = userScene.name;
        model.areaId = userScene.areaId;
        model.createdAt = userScene.createdAt;
        model.updatedAt = userScene.updatedAt;
        return model;
    }

    @NonNull
    public static UserScene map(@NonNull Model model) {
        UserScene userScene = new UserScene(model.userId, model.sceneId);
        userScene.name = model.name;
        userScene.areaId = model.areaId;
        userScene.createdAt = model.createdAt;
        userScene.updatedAt = model.updatedAt;
        return userScene;
    }

    @Parcel
    public static final class Model {

        String userId;

        String sceneId;

        String name;

        String areaId;

        String areaName;

        long createdAt = -1;

        long updatedAt = -1;
    }
}
