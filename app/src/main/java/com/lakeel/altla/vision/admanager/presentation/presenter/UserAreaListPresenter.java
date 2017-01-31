package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserAreaItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaListView;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreasUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class UserAreaListPresenter {

    private static final Log LOG = LogFactory.getLog(UserAreaListPresenter.class);

    private final List<UserAreaItemModel> items = new ArrayList<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    FindAllUserAreasUseCase findAllUserAreasUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    private UserAreaListView view;

    @Inject
    public UserAreaListPresenter() {
    }

    public void onCreateView(@NonNull UserAreaListView view) {
        this.view = view;
    }

    public void onStart() {
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
                    view.updateItem(items.size() - 1);
                }, e -> {
                    LOG.e("Failed to load all area description meta entries.", e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public int getItemCount() {
        return items.size();
    }

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public final class ItemPresenter {

        private UserAreaItemView itemView;

        public void onCreateItemView(@NonNull UserAreaItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            UserAreaItemModel model = items.get(position);
            itemView.showModel(model);
        }
    }
}
