package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionListView;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.helper.ObservableDataList;
import com.lakeel.altla.vision.domain.model.AreaDescription;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionListPresenter extends BasePresenter<UserAreaDescriptionListView>
        implements DataList.OnItemListener {

    @Inject
    VisionService visionService;

    private final DataList<Item> items = new DataList<>(this);

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

        Disposable disposable = ObservableDataList
                .using(() -> visionService.getUserAreaDescriptionApi().observeAllAreaDescriptions())
                .map(Event::new)
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

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickItem(int position) {
        Item item = items.get(position);
        getView().onItemSelected(item.areaDescription.getId());
    }

    public final class ItemPresenter {

        private UserAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull UserAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            Item item = items.get(position);
            itemView.onUpdateAreaDescriptionId(item.areaDescription.getId());
            itemView.onUpdateName(item.areaDescription.getName());
        }
    }

    private final class Event {

        final DataListEvent.Type type;

        final Item item;

        final String previousId;

        Event(@NonNull DataListEvent<AreaDescription> event) {
            type = event.getType();
            item = new Item(event.getData());
            previousId = event.getPreviousChildName();
        }
    }

    private final class Item implements DataList.Item {

        final AreaDescription areaDescription;

        Item(@NonNull AreaDescription areaDescription) {
            this.areaDescription = areaDescription;
        }

        @Override
        public String getId() {
            return areaDescription.getId();
        }
    }
}
