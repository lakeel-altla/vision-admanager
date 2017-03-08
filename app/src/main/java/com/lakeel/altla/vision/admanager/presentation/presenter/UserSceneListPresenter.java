package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserSceneListView;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.Scene;
import com.lakeel.altla.vision.domain.usecase.ObserveAllUserScenesUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserSceneListPresenter extends BasePresenter<UserSceneListView> implements DataList.OnItemListener {

    private final DataList<Item> items = new DataList<>(this);

    @Inject
    ObserveAllUserScenesUseCase observeAllUserScenesUseCase;

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

        Disposable disposable = observeAllUserScenesUseCase
                .execute()
                .map(Event::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.change(model.type, model.item, model.previousId);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    @Override
    public void onItemInserted(int index) {
        getView().onItemInserted(index);
    }

    @Override
    public void onItemChanged(int index) {
        getView().onItemChanged(index);
    }

    @Override
    public void onItemRemoved(int index) {
        getView().onItemRemoved(index);
    }

    @Override
    public void onItemMoved(int from, int to) {
        getView().onItemMoved(from, to);
    }

    @Override
    public void onDataSetChanged() {
        getView().onDataSetChanged();
    }

    public int getItemCount() {
        return items.size();
    }

    @NonNull
    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickItem(int position) {
        Item item = items.get(position);
        getView().onItemSelected(item.scene.getId());
    }

    public final class ItemPresenter {

        private UserSceneItemView itemView;

        public void onCreateItemView(@NonNull UserSceneItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            Item item = items.get(position);
            itemView.onUpdateSceneId(item.scene.getId());
            itemView.onUpdateName(item.scene.getName());
        }
    }

    private final class Event {

        final DataListEvent.Type type;

        final Item item;

        final String previousId;

        Event(@NonNull DataListEvent<Scene> event) {
            type = event.getType();
            item = new Item(event.getData());
            previousId = event.getPreviousChildName();
        }
    }

    private final class Item implements DataList.Item {

        final Scene scene;

        Item(@NonNull Scene scene) {
            this.scene = scene;
        }

        @Override
        public String getId() {
            return scene.getId();
        }
    }
}
