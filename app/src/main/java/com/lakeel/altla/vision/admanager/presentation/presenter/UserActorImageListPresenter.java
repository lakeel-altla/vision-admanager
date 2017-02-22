package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.module.Names;
import com.lakeel.altla.vision.admanager.presentation.view.UserActorImageItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserActorImageListView;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.UserActorImage;
import com.lakeel.altla.vision.domain.usecase.GetUserActorImageFileUriUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveAllUserActorImageUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserActorImageListPresenter extends BasePresenter<UserActorImageListView>
        implements DataList.OnItemListener {

    private final DataList<ItemModel> items = new DataList<>(this);

    @Inject
    ObserveAllUserActorImageUseCase observeAllUserActorImageUseCase;

    @Inject
    GetUserActorImageFileUriUseCase getUserActorImageFileUriUseCase;

    @Named(Names.ACTIVITY_CONTEXT)
    @Inject
    Context context;

    @Inject
    public UserActorImageListPresenter() {
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

        Disposable disposable = observeAllUserActorImageUseCase
                .execute()
                .map(this::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.change(model.type, model.item, model.previousAreaId);
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
        ItemModel model = items.get(position);
        getView().onItemSelected(model.imageId);
    }

    @NonNull
    private EventModel map(@NonNull DataListEvent<UserActorImage> event) {
        EventModel model = new EventModel();
        model.type = event.getType();
        model.item = map(event.getData());
        model.previousAreaId = event.getPreviousChildName();
        return model;
    }

    @NonNull
    private ItemModel map(@NonNull UserActorImage userActorImage) {
        ItemModel model = new ItemModel();
        model.imageId = userActorImage.imageId;
        model.name = userActorImage.name;
        return model;
    }

    public final class ItemPresenter {

        private UserActorImageItemView itemView;

        public void onCreateItemView(@NonNull UserActorImageItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            ItemModel model = items.get(position);
            itemView.onUpdateName(model.name);

            Disposable disposable = getUserActorImageFileUriUseCase
                    .execute(model.imageId)
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

    private final class EventModel {

        DataListEvent.Type type;

        String previousAreaId;

        ItemModel item;
    }

    private final class ItemModel implements DataList.Item {

        String imageId;

        String name;

        @Override
        public String getId() {
            return imageId;
        }
    }
}