package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.TangoSpaceItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceItemView;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceView;
import com.lakeel.altla.vision.domain.usecase.DeleteTangoAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.ExportUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAllTangoAreaDescriptionsUseCase;
import com.lakeel.altla.vision.domain.usecase.GetAreaDescriptionCacheDirectoryUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class TangoSpacePresenter implements TangoWrapper.OnTangoReadyListener {

    private static final Log LOG = LogFactory.getLog(TangoSpacePresenter.class);

    @Inject
    FindAllTangoAreaDescriptionsUseCase findAllTangoAreaDescriptionsUseCase;

    @Inject
    GetAreaDescriptionCacheDirectoryUseCase getAreaDescriptionCacheDirectoryUseCase;

    @Inject
    ExportUserAreaDescriptionUseCase exportUserAreaDescriptionUseCase;

    @Inject
    DeleteTangoAreaDescriptionUseCase deleteTangoAreaDescriptionUseCase;

    private final List<TangoSpaceItemModel> itemModels = new ArrayList<>();

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private TangoWrapper tangoWrapper;

    private TangoSpaceView view;

    private String exportingAreaDescriptionId;

    @Inject
    public TangoSpacePresenter() {
    }

    @Override
    public void onTangoReady(Tango tango) {
        Subscription subscription = findAllTangoAreaDescriptionsUseCase
                .execute(tangoWrapper.getTango())
                .map(TangoSpaceItemModelMapper::map)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemModels -> {
                    this.itemModels.clear();
                    this.itemModels.addAll(itemModels);
                    view.updateItems();
                }, e -> {
                    LOG.e("Loading area description meta datas failed.", e);
                });
        compositeSubscription.add(subscription);
    }

    public void onCreate(@NonNull TangoWrapper tangoWrapper) {
        this.tangoWrapper = tangoWrapper;
    }

    public void onCreateView(@NonNull TangoSpaceView view) {
        this.view = view;
    }

    public void onStop() {
        compositeSubscription.clear();
    }

    public void onResume() {
        tangoWrapper.addOnTangoReadyListener(this);
    }

    public void onPause() {
        tangoWrapper.removeOnTangoReadyListener(this);
    }

    public void onCreateItemView(@NonNull TangoSpaceItemView itemView) {
        TangoSpaceItemPresenter itemPresenter = new TangoSpaceItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return itemModels.size();
    }

    public void onExported() {
        if (exportingAreaDescriptionId == null) {
            throw new IllegalStateException("'exportingAreaDescriptionId' is null.");
        }

        Subscription subscription = exportUserAreaDescriptionUseCase
                .execute(tangoWrapper.getTango(), exportingAreaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userAreaDescription -> {
                    view.showSnackbar(R.string.snackbar_done);
                }, e -> {
                    LOG.e(String.format("Failed to export the tango area description: areaDescriptionId = %s",
                                        exportingAreaDescriptionId), e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeSubscription.add(subscription);

        view.showSnackbar(R.string.snackbar_done);
    }

    public void onDelete(int position) {
        String areaDescriptionId = itemModels.get(position).areaDescriptionId;

        Subscription subscription = deleteTangoAreaDescriptionUseCase
                .execute(tangoWrapper.getTango(), areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    itemModels.remove(position);
                    view.updateItemRemoved(position);
                    view.showSnackbar(R.string.snackbar_done);
                }, e -> {
                    LOG.e(String.format("Failed to delete the tango area description: areaDescriptionId = %s",
                                        areaDescriptionId), e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeSubscription.add(subscription);
    }

    public final class TangoSpaceItemPresenter {

        private TangoSpaceItemView itemView;

        public void onCreateItemView(@NonNull TangoSpaceItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            TangoSpaceItemModel itemModel = itemModels.get(position);
            itemView.showModel(itemModel);
        }

        public void onClickImageButtonExport(int position) {
            Subscription subscription = getAreaDescriptionCacheDirectoryUseCase
                    .execute()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(directory -> {
                        exportingAreaDescriptionId = itemModels.get(position).areaDescriptionId;
                        view.showExportActivity(exportingAreaDescriptionId, directory);
                    });
            compositeSubscription.add(subscription);
        }

        public void onClickImageButtonDelete(int position) {
            view.showDeleteConfirmationDialog(position);
        }
    }
}
