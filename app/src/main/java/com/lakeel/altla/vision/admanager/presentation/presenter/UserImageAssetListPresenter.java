package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.module.Names;
import com.lakeel.altla.vision.admanager.presentation.view.UserImageAssetItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserImageAssetListView;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.ImageAsset;
import com.lakeel.altla.vision.domain.usecase.GetUserImageAssetFileUriUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveAllUserImageAssetUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserImageAssetListPresenter extends BasePresenter<UserImageAssetListView>
        implements DataList.OnItemListener {

    private final DataList<Item> items = new DataList<>(this);

    @Inject
    ObserveAllUserImageAssetUseCase observeAllUserImageAssetUseCase;

    @Inject
    GetUserImageAssetFileUriUseCase getUserImageAssetFileUriUseCase;

    @Named(Names.ACTIVITY_CONTEXT)
    @Inject
    Context context;

    @Inject
    public UserImageAssetListPresenter() {
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(R.string.title_user_actor_image_list);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();

        Disposable disposable = observeAllUserImageAssetUseCase
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
        getView().onItemSelected(item.asset.getId());
    }

    public final class ItemPresenter {

        private UserImageAssetItemView itemView;

        public void onCreateItemView(@NonNull UserImageAssetItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            Item item = items.get(position);
            itemView.onUpdateName(item.asset.getName());

            Disposable disposable = getUserImageAssetFileUriUseCase
                    .execute(item.asset.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uri -> {
                        getLog().v("UserActorImageFile: uri = %s", uri);
                        itemView.onUpdateThumbnail(uri);
                    }, e -> {
                        getLog().e("Failed.", e);
                    });
            manageDisposable(disposable);
        }
    }

    private final class Event {

        final DataListEvent.Type type;

        final Item item;

        final String previousId;

        Event(@NonNull DataListEvent<ImageAsset> event) {
            type = event.getType();
            item = new Item(event.getData());
            previousId = event.getPreviousChildName();
        }
    }

    private final class Item implements DataList.Item {

        final ImageAsset asset;

        Item(@NonNull ImageAsset asset) {
            this.asset = asset;
        }

        @Override
        public String getId() {
            return asset.getId();
        }
    }
}