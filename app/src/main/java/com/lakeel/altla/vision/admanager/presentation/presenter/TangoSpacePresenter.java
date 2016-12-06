package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.domain.usecase.appspace.GetAreaDescriptionCacheDirectoryUseCase;
import com.lakeel.altla.vision.admanager.domain.usecase.tangospace.AddAreaDescriptionUseCase;
import com.lakeel.altla.vision.admanager.domain.usecase.tangospace.DeleteTangoAreaDescriptionUseCase;
import com.lakeel.altla.vision.admanager.domain.usecase.tangospace.FindAllTangoAreaDescriptionUseCase;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.TangoSpaceItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceItemView;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceView;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class TangoSpacePresenter {

    private static final Log LOG = LogFactory.getLog(TangoSpacePresenter.class);

    @Inject
    FindAllTangoAreaDescriptionUseCase findAllTangoAreaDescriptionUseCase;

    @Inject
    GetAreaDescriptionCacheDirectoryUseCase getAreaDescriptionCacheDirectoryUseCase;

    @Inject
    AddAreaDescriptionUseCase addAreaDescriptionUseCase;

    @Inject
    DeleteTangoAreaDescriptionUseCase deleteTangoAreaDescriptionUseCase;

    private final List<TangoSpaceItemModel> itemModels = new ArrayList<>();

    private final TangoSpaceItemModelMapper mapper = new TangoSpaceItemModelMapper();

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private TangoSpaceView view;

    private String exportingId;

    private long prevBytesTransferred;

    @Inject
    public TangoSpacePresenter() {
    }

    public void onCreateView(@NonNull TangoSpaceView view) {
        this.view = view;
    }

    public void onStart() {
        Subscription subscription = findAllTangoAreaDescriptionUseCase
                .execute()
                .map(mapper::map)
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

    public void onStop() {
        compositeSubscription.clear();
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
        if (exportingId == null) {
            throw new IllegalStateException("exportingUuid == null");
        }

        LOG.d("Adding the area description: id = %s", exportingId);

        prevBytesTransferred = 0;
        view.showUploadProgressDialog();

        Subscription subscription = addAreaDescriptionUseCase
                .execute(exportingId, (totalBytes, bytesTransferred) -> {
                    long increment = bytesTransferred - prevBytesTransferred;
                    prevBytesTransferred = bytesTransferred;
                    view.setUploadProgressDialogProgress(totalBytes, increment);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(entry -> {
                               LOG.d("Added the area description.");
                               view.hideUploadProgressDialog();
                               view.showSnackbar(R.string.snackbar_done);
                           }, e -> {
                               LOG.e(String.format("Failed to add the area description: id = %s", exportingId), e);
                               view.hideUploadProgressDialog();
                               view.showSnackbar(R.string.snackbar_failed);
                           }

                );
        compositeSubscription.add(subscription);
    }

    public void onDelete(@IntRange(from = 0) int position) {
        String uuid = itemModels.get(position).id;

        Subscription subscription = deleteTangoAreaDescriptionUseCase
                .execute(uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    itemModels.remove(position);
                    view.updateItemRemoved(position);
                    view.showSnackbar(R.string.snackbar_done);
                }, e -> {
                    LOG.e("Deleting area description failed.", e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeSubscription.add(subscription);
    }

    public final class TangoSpaceItemPresenter {

        private TangoSpaceItemView itemView;

        public void onCreateItemView(@NonNull TangoSpaceItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(@IntRange(from = 0) int position) {
            TangoSpaceItemModel itemModel = itemModels.get(position);
            itemView.showModel(itemModel);
        }

        public void onExport(@IntRange(from = 0) int position) {
            Subscription subscription = getAreaDescriptionCacheDirectoryUseCase
                    .execute()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(directory -> {
                        exportingId = itemModels.get(position).id;
                        view.showExportActivity(exportingId, directory);
                    });
            compositeSubscription.add(subscription);
        }
    }
}
