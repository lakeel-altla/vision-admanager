package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionItemView;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionListView;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;
import com.lakeel.altla.vision.domain.usecase.FindAllTangoAreaDescriptionsUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class TangoAreaDescriptionListPresenter extends BasePresenter<TangoAreaDescriptionListView>
        implements TangoWrapper.OnTangoReadyListener {

    @Inject
    FindAllTangoAreaDescriptionsUseCase findAllTangoAreaDescriptionsUseCase;

    @Inject
    TangoWrapper tangoWrapper;

    private final List<ItemModel> items = new ArrayList<>();

    @Inject
    public TangoAreaDescriptionListPresenter() {
    }

    @Override
    public void onTangoReady(Tango tango) {
        Disposable disposable = findAllTangoAreaDescriptionsUseCase
                .execute(tangoWrapper.getTango())
                .map(this::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.add(model);
                    getView().onItemInserted(items.size() - 1);
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(R.string.title_tango_area_description_list);
    }

    @Override
    protected void onResumeOverride() {
        super.onResumeOverride();

        items.clear();
        getView().onItemsUpdated();

        tangoWrapper.addOnTangoReadyListener(this);
    }

    @Override
    protected void onPauseOverride() {
        super.onPauseOverride();

        tangoWrapper.removeOnTangoReadyListener(this);
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
    private ItemModel map(@NonNull TangoAreaDescription tangoAreaDescription) {
        ItemModel model = new ItemModel();
        model.areaDescriptionId = tangoAreaDescription.areaDescriptionId;
        model.name = tangoAreaDescription.name;
        return model;
    }

    public final class ItemPresenter {

        private TangoAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull TangoAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            ItemModel model = items.get(position);
            itemView.onUpdateAreaDescriptionId(model.areaDescriptionId);
            itemView.onUpdateName(model.name);
        }
    }

    private final class ItemModel {

        String areaDescriptionId;

        String name;
    }
}
