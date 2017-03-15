package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionItemView;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionListView;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public final class TangoAreaDescriptionListPresenter extends BasePresenter<TangoAreaDescriptionListView>
        implements TangoWrapper.OnTangoReadyListener {

    @Inject
    VisionService visionService;

    private final List<TangoAreaDescription> items = new ArrayList<>();

    @Inject
    public TangoAreaDescriptionListPresenter() {
    }

    @Override
    public void onTangoReady(Tango tango) {
        runOnUiThread(() -> {
            List<TangoAreaDescription> tangoAreaDescriptions = visionService.getTangoAreaDescriptionApi()
                                                                            .findAllTangoAreaDescriptions();

            items.clear();
            items.addAll(tangoAreaDescriptions);
            getView().onDataSetChanged();
        });
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
        getView().onDataSetChanged();

        visionService.getTangoWrapper().addOnTangoReadyListener(this);
    }

    @Override
    protected void onPauseOverride() {
        super.onPauseOverride();

        visionService.getTangoWrapper().removeOnTangoReadyListener(this);
    }

    public int getItemCount() {
        return items.size();
    }

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickItem(int position) {
        TangoAreaDescription tangoAreaDescription = items.get(position);
        getView().onItemSelected(tangoAreaDescription.getAreaDescriptionId());
    }

    public final class ItemPresenter {

        private TangoAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull TangoAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            TangoAreaDescription tangoAreaDescription = items.get(position);
            itemView.onUpdateAreaDescriptionId(tangoAreaDescription.getAreaDescriptionId());
            itemView.onUpdateName(tangoAreaDescription.getName());
        }
    }
}
