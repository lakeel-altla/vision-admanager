package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaSelectView;
import com.lakeel.altla.vision.domain.model.Area;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreasUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaSelectPresenter extends BasePresenter<UserAreaSelectView> {

    private final List<Item> items = new ArrayList<>();

    @Inject
    FindAllUserAreasUseCase findAllUserAreasUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    public UserAreaSelectPresenter() {
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(R.string.title_select_user_area);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();
        getView().onItemsUpdated();

        Disposable disposable = findAllUserAreasUseCase
                .execute()
                .map(Item::new)
                .concatMap(item -> {
                    if (item.area.getPlaceId() == null) {
                        return Observable.just(item);
                    } else {
                        return getPlaceUseCase
                                .execute(item.area.getPlaceId())
                                .map(place -> {
                                    item.placeName = place.getName().toString();
                                    item.placeAddress = place.getAddress().toString();
                                    return item;
                                })
                                .toObservable();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    items.add(item);
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

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickItem(int position) {
        Item item = items.get(position);
        getView().onItemSelected(item.area.getId());
    }

    public final class ItemPresenter {

        private UserAreaItemView itemView;

        public void onCreateItemView(@NonNull UserAreaItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            Item item = items.get(position);
            itemView.onUpdateAreaId(item.area.getId());
            itemView.onUpdateName(item.area.getName());
            itemView.onUpdatePlaceName(item.placeName);
            itemView.onUpdatePladeAddress(item.placeAddress);
            itemView.onUpdateLevel(item.area.getLevel());
        }
    }

    private final class Item {

        final Area area;

        String placeName;

        String placeAddress;

        Item(@NonNull Area area) {
            this.area = area;
        }
    }
}
