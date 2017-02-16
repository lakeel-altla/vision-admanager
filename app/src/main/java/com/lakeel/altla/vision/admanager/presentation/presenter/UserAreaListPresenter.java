package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaListView;
import com.lakeel.altla.vision.domain.model.UserArea;
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

public final class UserAreaListPresenter extends BasePresenter<UserAreaListView> {

    private final List<ItemModel> items = new ArrayList<>();

    @Inject
    FindAllUserAreasUseCase findAllUserAreasUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    public UserAreaListPresenter() {
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(R.string.title_user_area_list);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();
        getView().onItemsUpdated();

        Disposable disposable = findAllUserAreasUseCase
                .execute()
                .map(this::map)
                .concatMap(model -> {
                    if (model.placeId == null) {
                        return Observable.just(model);
                    } else {
                        return getPlaceUseCase
                                .execute(model.placeId)
                                .map(place -> {
                                    model.placeName = place.getName().toString();
                                    model.placeAddress = place.getAddress().toString();
                                    return model;
                                })
                                .toObservable();
                    }
                })
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

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickItem(int position) {
        ItemModel model = items.get(position);
        getView().onItemSelected(model.areaId);
    }

    @NonNull
    private ItemModel map(@NonNull UserArea userArea) {
        ItemModel model = new ItemModel();
        model.areaId = userArea.areaId;
        model.name = userArea.name;
        model.placeId = userArea.placeId;
        model.level = userArea.level;
        return model;
    }

    public final class ItemPresenter {

        private UserAreaItemView itemView;

        public void onCreateItemView(@NonNull UserAreaItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            ItemModel model = items.get(position);
            itemView.onUpdateAreaId(model.areaId);
            itemView.onUpdateName(model.name);
            itemView.onUpdatePlaceName(model.placeName);
            itemView.onUpdatePladeAddress(model.placeAddress);
            itemView.onUpdateLevel(model.level);
        }
    }

    private final class ItemModel {

        String areaId;

        String name;

        String placeId;

        String placeName;

        String placeAddress;

        int level;
    }
}
