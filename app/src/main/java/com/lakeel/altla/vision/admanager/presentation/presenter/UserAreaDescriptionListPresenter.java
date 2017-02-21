package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionListView;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.usecase.ObserveAllUserAreaDescriptionsUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionListPresenter extends BasePresenter<UserAreaDescriptionListView>
        implements DataList.OnItemListener {

    @Inject
    ObserveAllUserAreaDescriptionsUseCase observeAllUserAreaDescriptionsUseCase;

    private final DataList<ItemModel> items = new DataList<>(this);

    @Inject
    public UserAreaDescriptionListPresenter() {
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(R.string.title_user_area_description_list);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();

        Disposable disposable = observeAllUserAreaDescriptionsUseCase
                .execute()
                .map(this::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.change(model.type, model.item, model.previousAreaDescriptionId);
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

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickItem(int position) {
        ItemModel model = items.get(position);
        getView().onItemSelected(model.areaDescriptionId);
    }

    @NonNull
    private EventModel map(@NonNull DataListEvent<UserAreaDescription> event) {
        EventModel model = new EventModel();
        model.type = event.getType();
        model.item = map(event.getData());
        model.previousAreaDescriptionId = event.getPreviousChildName();
        return model;
    }

    @NonNull
    private ItemModel map(@NonNull UserAreaDescription userAreaDescription) {
        ItemModel model = new ItemModel();
        model.areaDescriptionId = userAreaDescription.areaDescriptionId;
        model.name = userAreaDescription.name;
        return model;
    }

    public final class ItemPresenter {

        private UserAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull UserAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            ItemModel model = items.get(position);
            itemView.onUpdateAreaDescriptionId(model.areaDescriptionId);
            itemView.onUpdateName(model.name);
        }
    }

    private final class EventModel {

        DataListEvent.Type type;

        String previousAreaDescriptionId;

        ItemModel item;
    }

    private final class ItemModel implements DataList.Item {

        String areaDescriptionId;

        String name;

        @Override
        public String getId() {
            return areaDescriptionId;
        }
    }
}
