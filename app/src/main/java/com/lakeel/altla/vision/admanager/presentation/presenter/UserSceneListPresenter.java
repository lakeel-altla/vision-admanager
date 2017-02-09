package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserAreaItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserSceneItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserSceneItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneListView;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreasUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAllUserScenesUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserSceneListPresenter extends BasePresenter<UserSceneListView> {

    private final List<UserSceneItemModel> items = new ArrayList<>();

    @Inject
    FindAllUserScenesUseCase findAllUserScenesUseCase;

    @Inject
    public UserSceneListPresenter() {
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();
        getView().onItemsUpdated();

        Disposable disposable = findAllUserScenesUseCase
                .execute()
                .map(UserSceneItemModelMapper::map)
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
        UserSceneItemModel model = items.get(position);
        getView().onItemSelected(model.sceneId);
    }

    public final class ItemPresenter {

        private UserSceneItemView itemView;

        public void onCreateItemView(@NonNull UserSceneItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            UserSceneItemModel model = items.get(position);
            itemView.onModelUpdated(model);
        }
    }
}
