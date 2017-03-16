package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.android.gms.common.api.GoogleApiClient;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaSelectView;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public final class UserAreaSelectPresenter extends BasePresenter<UserAreaSelectView> {

    @Inject
    VisionService visionService;

    @Inject
    GoogleApiClient googleApiClient;

    private final List<Item> items = new ArrayList<>();

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

        Disposable disposable = Observable.<Item>create(e -> {
            visionService.getUserAreaApi().findAllAreas(areas -> {
                for (Area area : areas) {
                    e.onNext(new Item(area));
                }
                e.onComplete();
            }, e::onError);
        }).concatMap(item -> {
            String placeId = item.area.getPlaceId();
            if (placeId == null) {
                return Observable.just(item);
            } else {
                return Observable.create(e -> {
                    visionService.getGooglePlaceApi().getPlaceById(googleApiClient, placeId, place -> {
                        item.placeName = place.getName().toString();
                        item.placeAddress = place.getAddress().toString();
                        e.onNext(item);
                        e.onComplete();
                    }, e::onError);
                });
            }
        }).subscribe(item -> {
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
