package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserAreaItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.SelectUserAreaView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaItemView;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreasUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class SelectUserAreaPresenter extends BasePresenter<SelectUserAreaView> {

    private final List<UserAreaItemModel> items = new ArrayList<>();

    @Inject
    FindAllUserAreasUseCase findAllUserAreasUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    public SelectUserAreaPresenter() {
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();
        getView().onItemsUpdated();

        Disposable disposable = findAllUserAreasUseCase
                .execute()
                .map(UserAreaItemModelMapper::map)
                .concatMap(model -> {
                    if (model.placeId != null) {
                        return getPlaceUseCase.execute(model.placeId)
                                              .map(place -> UserAreaItemModelMapper.map(model, place))
                                              .toObservable();
                    } else {
                        return Observable.just(model);
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
        UserAreaItemModel model = items.get(position);
        getView().onItemSelected(model.areaId);
    }

    public final class ItemPresenter {

        private UserAreaItemView itemView;

        public void onCreateItemView(@NonNull UserAreaItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            UserAreaItemModel model = items.get(position);
            itemView.onModelUpdated(model);
        }
    }
}
