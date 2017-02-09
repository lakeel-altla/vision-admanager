package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserSceneModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserSceneModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneEditView;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserScene;
import com.lakeel.altla.vision.domain.usecase.FindUserSceneUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserSceneUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class UserSceneEditPresenter extends BasePresenter<UserSceneEditView> {

    private static final String ARG_SCENE_ID = "sceneId";

    @Inject
    FindUserSceneUseCase findUserSceneUseCase;

    @Inject
    SaveUserSceneUseCase saveUserSceneUseCase;

    @Inject
    CurrentUserResolver currentUserResolver;

    private String sceneId;

    private UserSceneModel model;

    private boolean processing;

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
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        if (sceneId != null) {
            processing = true;

            Disposable disposable = findUserSceneUseCase
                    .execute(sceneId)
                    .map(UserSceneModelMapper::map)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(model -> processing = false)
                    .doOnComplete(() -> processing = false)
                    .doOnError(e -> processing = false)
                    .subscribe(model -> {
                        this.model = model;
                        getView().onUpdateTitle(model.name);
                        getView().onModelUpdated(model);
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            manageDisposable(disposable);
        } else {
            sceneId = UUID.randomUUID().toString();
            model = new UserSceneModel(currentUserResolver.getUserId(), sceneId);

            getView().onModelUpdated(model);
        }
    }

    public void onEditTextNameAfterTextChanged(String name) {
        if (processing) return;
        if (name != null && name.length() == 0) {
            name = null;
        }
        if (model.name == null && name == null) return;
        if (name != null && name.equals(model.name)) return;
        if (model.name != null && model.name.equals(name)) return;

        processing = true;

        model.name = name;

        save();
    }

    private void save() {
        UserScene userScene = UserSceneModelMapper.map(model);

        Disposable disposable = saveUserSceneUseCase
                .execute(userScene)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> processing = false)
                .subscribe(() -> {
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);

        getView().onUpdateTitle(model.name);
    }
}
