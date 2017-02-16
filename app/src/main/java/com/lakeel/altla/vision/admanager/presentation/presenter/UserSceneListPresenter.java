package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneListView;
import com.lakeel.altla.vision.domain.model.UserScene;
import com.lakeel.altla.vision.domain.usecase.FindAllUserScenesUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserSceneListPresenter extends BasePresenter<UserSceneListView> {

    private final List<ItemModel> items = new ArrayList<>();

    @Inject
    FindAllUserScenesUseCase findAllUserScenesUseCase;

    @Inject
    public UserSceneListPresenter() {
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(R.string.title_user_scene_list);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();
        getView().onItemsUpdated();

        Disposable disposable = findAllUserScenesUseCase
                .execute()
                .map(this::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.add(model);
                    getView().onItemInserted(items.size() - 1);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public int getItemCount() {
        return items.size();
    }

    @NonNull
    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickItem(int position) {
        ItemModel model = items.get(position);
        getView().onItemSelected(model.sceneId);
    }

    @NonNull
    private ItemModel map(@NonNull UserScene userScene) {
        ItemModel model = new ItemModel();
        model.sceneId = userScene.sceneId;
        model.name = userScene.name;
        return model;
    }

    public final class ItemPresenter {

        private UserSceneItemView itemView;

        public void onCreateItemView(@NonNull UserSceneItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            ItemModel model = items.get(position);
            itemView.onUpdateSceneId(model.sceneId);
            itemView.onUpdateName(model.name);
        }
    }

    private final class ItemModel {

        String sceneId;

        String name;
    }
}
